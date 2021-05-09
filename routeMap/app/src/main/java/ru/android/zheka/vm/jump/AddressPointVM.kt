package ru.android.zheka.vm.jump

import android.Manifest
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Response
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.coreUI.OnFocusCallback
import ru.android.zheka.coreUI.SpinnerHandler
import ru.android.zheka.db.Config
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.fragment.EnterPoint
import ru.android.zheka.fragment.IEnterPoint
import ru.android.zheka.fragment.JumpPoint
import ru.android.zheka.geo.GeoCoder
import ru.android.zheka.geo.GeoParserImpl
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.AddressModel
import ru.android.zheka.model.AddressModel.Companion.aDelimiter
import ru.android.zheka.model.FocusData
import ru.android.zheka.model.IAddressModel
import ru.android.zheka.model.IEnterPointModel
import ru.android.zheka.sound.SoundParser
import ru.android.zheka.sound.response.ResponseError
import ru.android.zheka.sound.response.SoundResponse
import ru.android.zheka.sound.util.AudioRecorder

class AddressPointVM(val view: IEnterPoint, val model: IAddressModel) : IAddressPointVM {
    private lateinit var errHandler: ErrorHandler
    private val _regId = R.id.text_region_1
    private val _cityId = R.id.text_city_1
    private val _streetId = R.id.text_street_1
    private val _houseId = R.id.text_house_1
    private var response:String? = null
    private val speechSrv:SoundParser = SoundParser()
    private val rawFileName = (view as EnterPoint).context!!.getExternalFilesDir(null)!!.parent+"/record"

    override lateinit var panelModel: IPanelModel

    override fun options(): List<String> {
        val res = view.activity.resources
        var selectedItem = res.getString(R.string.enter_point_address)
        var anotherItem = res.getString(R.string.enter_point_address_en)
        if (isUk()){
            val tmp = selectedItem
            selectedItem = anotherItem
            anotherItem = tmp
        }
        return listOf(selectedItem,
                res.getString(R.string.enter_point_coordinate),
                anotherItem)
    }

    override fun switchFragment(fragment: EnterPoint, option: String) {
        val triggered = model.spinnerOptions == option
        AddressModel.spinnerOptions = option
        if (!triggered) {
            view.switchToFragment(R.id.latLngFragment, EnterPoint())
        }
    }
    class ErrorHandler(val vm:AddressPointVM): Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            Observable.just(true).subscribe({throw RuntimeException(msg.obj as String) }, vm.view::showError)
        }
    }

    override fun onClick() {
//        if (isAllFieldCorrect) {
        ViewModelProvider(view as Fragment).get(RecordViewModel::class.java)
                .viewModelScope.launch(Dispatchers.IO) {
            updateDb()
            val geoCoder:GeoCoder = try {
                GeoParserImpl(model.region.get()
                        , model.city.get()
                        , model.street.get()
                        , model.house.get())
                        .parse((view as Fragment).activity?.resources?.getString(R.string.yandex_geo_codec)!!)
            } catch (e: GeoParserImpl.YandexGeoCoderException) {
                sendErroToMain("Данные не получены, проверьте интернет соединение")
                return@launch
            } catch (e: Exception) {
                sendErroToMain(e.message)
                return@launch
            }
            AddressModel.geoCoder = geoCoder
            view.removeFragment(view as Fragment)
            view.switchToFragment(R.id.latLngFragment, JumpPoint())
        }
            return
//        }
//        throw java.lang.RuntimeException("Имеются пустые поля, используйте \"-\" для них")
    }

    private fun updateDb() {
        val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config
        config.address = StringBuilder().append(model.region.get()).append(aDelimiter)
                .append(model.city.get()).append(aDelimiter)
                .append(model.street.get()).append(aDelimiter)
                .append(model.house.get()).toString()
        try {
            DbFunctions.add(config)
        } catch (e: IllegalAccessException) {
        } catch (e: InstantiationException) {
            // not critical
        }
    }

    private val isAllFieldCorrect: Boolean
        private get() {
            if (model.region.get()!!.isEmpty()) return false
            if (model.city.get()!!.isEmpty()) return false
            if (model.street.get()!!.isEmpty()) return false
            return !model.house.get()!!.isEmpty()
        }

    override fun onResume() {
        model.visiability.set(IEnterPointModel.ADDRESS_ON)
        panelModel.action().set("Допускается пропускать поля")
        panelModel.inputVisible().set(IPanelModel.COMBO_BOX_VISIBLE)
        panelModel.spinner.set(SpinnerHandler(Consumer{
            switchFragment(view as EnterPoint, it)
        }, Consumer{}, options(), view))
        panelModel.nextButton2.set(ButtonHandler(Consumer{ onClick() }, R.string.home_address_btn, view))
        model.clearButton.set(ButtonHandler(Consumer{ clear() }, R.string.address_clear, view))
        model.recordButton.set(ButtonHandler(Consumer{ record() }, R.string.address_record, view))
        model.backButton.set(ButtonHandler(Consumer{ back()}, R.string.address_repeat_btn, view))
        model.onFocusLost = this.recordSoundToField()
        controlVoice(View.GONE)
        errHandler = ErrorHandler(this)
        updateUIModel()
    }

    private fun controlVoice(visiable:Int) {
        model.recordButton.get()?.visible?.set(visiable)
        model.backButton.get()?.visible?.set(visiable)
    }

    private fun back() {
        model.apply {
            if (focusReg.get()) {
                focusHouse.set(true)
            }else if (focusCity.get()){
                focusReg.set(true)
            }else if (focusStreet.get()) {
                focusCity.set(true)
            }else if (focusHouse.get()) {
                focusStreet.set(true)
            }
        }
    }

    class ResponseHolder (val vm:AddressPointVM, val v:View):retrofit2.Callback<SoundResponse>{
        override fun onFailure(call: retrofit2.Call<SoundResponse>, t: Throwable) {
            Observable.just(true).subscribe({throw t},vm.view::showError)
        }

        override fun onResponse(call: retrofit2.Call<SoundResponse>, response: Response<SoundResponse>) {
            Observable.just(true).subscribe({
                if (response.body() != null) {
                    if (!response.body()!!.isInitialized())
                        throw RuntimeException(vm.view.context().getString(R.string.err_emptyVoiceBody))
                    val results = response.body()!!.results
                    if (results.isEmpty() || !results.get(0).isInitialized())
                        throw RuntimeException(vm.view.context().getString(R.string.err_emptyVoiceResult))
                    val alternatives = results.get(0).alternatives
                    vm.response = alternatives.get(0).toString()
                    recordResponse()
                    return@subscribe
                }
                val bytes = response.errorBody()?.bytes()!!
                val error = Gson().fromJson(String(bytes), ResponseError::class.java)
                if (!error.isInitialized())
                    throw RuntimeException(vm.view.context().getString(R.string.err_emptyVoiceUndef))
                throw java.lang.RuntimeException(error.error.toString())
            }, vm.view::showError)
        }

        private fun recordResponse() {
            vm.apply {
                recordField(v.id, response ?: "null")
                response = null
            }
        }
    }

    private fun recordSoundToField() =  FocusData(Consumer{ v ->
        focusItem(v.id, false)
        if (this::recorder.isInitialized && !recorder.isReleased) { // make light
            Observable.just(true).subscribe({
                val bytes = recorder.getmWaveFile().readBytes()
                val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP) //no eol
                val lan = if (isUk()) SoundParser.EN_CODE else SoundParser.RU_CODE
                speechSrv.buildContent(base64)
                        .parse(view.context().getString(R.string.SPEECH_KEY), lan)
                        .enqueue(ResponseHolder(this, v))
            }, view::showError)
        }
        if (this::recorder.isInitialized) recorder.release()
    }, Consumer { v ->
        focusItem(v.id, true)
        controlVoice(View.VISIBLE)
    })

    private fun isUk(): Boolean {
        return AddressModel.spinnerOptions == view.context().getString(R.string.enter_point_address_en)
    }

    private fun recordField(id: Int, msg: String) {
        if (msg != "null") {
            when (id) {
                _cityId -> {
                    model.city.set(msg)
                }
                _streetId -> {
                    model.street.set(msg)
                }
                _houseId -> {
                    model.house.set(msg)
                }
                _regId -> {
                    model.region.set(msg)
                }
            }
            updateDb()
        }
    }

    private fun focusItem(id:Int, isFocus:Boolean) {
        when(id) {
            _cityId -> { model.focusCity.set(isFocus) }
            _streetId -> { model.focusStreet.set(isFocus) }
            _houseId -> { model.focusHouse.set(isFocus) }
            _regId -> { model.focusReg.set(isFocus) }
        }
    }

    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private lateinit var recorder: AudioRecorder

    class RecordViewModel: ViewModel {
        public constructor()
    }

    private fun record() {
        ActivityCompat.requestPermissions(view.activity, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        val viewModel = ViewModelProvider(view as Fragment).get(RecordViewModel::class.java)

        viewModel.viewModelScope.launch {
            delay(3000)
            recorder.apply {
                stop()
            }
            model.apply {
                if (focusReg.get()) {
                    focusCity.set(true)
                }else if (focusCity.get()){
                    focusStreet.set(true)
                }else if (focusStreet.get()) {
                    focusHouse.set(true)
                }else if (focusHouse.get()) {
                    focusReg.set(true)
                }
            }
        }
        recorder = AudioRecorder(rawFileName).apply {
            viewModel.viewModelScope.launch(Dispatchers.IO) {
                try{
                    start()
                }catch (e:Exception){
                    sendErroToMain(e.message)
                    release()
                }
            }
        }
        panelModel.action().set("Запись ведется 3 секунды ")
    }

    private fun sendErroToMain(message: String?) {
        errHandler.obtainMessage(1,message).sendToTarget()
    }

    override fun clear() {
        val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config
        config.address = aDelimiter + aDelimiter + aDelimiter
        DbFunctions.add(config)
        updateUIModel()
    }

    private fun updateUIModel() {
        val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config
        if (config.tenMSTime != "0") Toast.makeText(view.context(), "Определение местоположения станет помехой, лучше его отключить", 30).show()
        var address = config.address
        if ((address?:"").isNotEmpty()) {
            var endpos = address!!.indexOf(aDelimiter)
            model.region.set(address.substring(0, endpos))
            address = address.substring(endpos + 1)
            endpos = address.indexOf(aDelimiter)
            model.city.set(address.substring(0, endpos))
            address = address.substring(endpos + 1)
            endpos = address.indexOf(aDelimiter)
            model.street.set(address.substring(0, endpos))
            model.house.set(address.substring(endpos + 1))
        }
    }

    override fun model(): IAddressModel {
        return model
    }

    override fun onDestroy() {
        panelModel.nextButton2.get()?.visible?.set(View.GONE)
        panelModel.inputVisible().set(View.GONE)
        panelModel.action().set("")
    }
}
    @BindingAdapter("app:requestFocus")
    fun requestFocus(view: View, requestFocus: Boolean){
        if(requestFocus){
            view.apply { isFocusableInTouchMode = true
                requestFocus()
            }
        }
    }
    @BindingAdapter("app:onFocusLost")
    fun onFocusLost(view:View, callback: OnFocusCallback) {
        view.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) { callback.onFocusLost(view as EditText)
                return@setOnFocusChangeListener
            }
            callback.onFocus(view as EditText)
        }
    }
