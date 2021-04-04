package ru.android.zheka.vm.jump

import android.Manifest
import android.media.MediaRecorder
import android.os.Build
import android.util.Base64
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
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
import ru.android.zheka.geo.GeoParserImpl
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.AddressModel
import ru.android.zheka.model.AddressModel.Companion.aDelimiter
import ru.android.zheka.model.FocusData
import ru.android.zheka.model.IAddressModel
import ru.android.zheka.model.IEnterPointModel
import ru.android.zheka.sound.response.ResponseError
import ru.android.zheka.sound.SoundParser
import ru.android.zheka.sound.response.SoundResponse
import java.io.File

class AddressPointVM(val view: IEnterPoint, val model: IAddressModel) : IAddressPointVM {

    override lateinit var panelModel: IPanelModel

    override fun options(): List<String> {
        val res = view.activity.resources
        return listOf<String>(res.getString(R.string.enter_point_address),
                res.getString(R.string.enter_point_coordinate))
    }

    override fun switchFragment(fragment: EnterPoint, option: String) {
        val triggered = model.spinnerOptions == option
        AddressModel.spinnerOptions = option
        if (!triggered) {
            view.switchToFragment(R.id.latLngFragment, EnterPoint())
        }
    }

    override fun onClick() {
//        if (isAllFieldCorrect) {
            Observable.just(true).observeOn(Schedulers.io())
                    .subscribe({
                        updateDb()
                        val geoCoder = try {
                            GeoParserImpl(model.region.get()
                                    , model.city.get()
                                    , model.street.get()
                                    , model.house.get())
                                    .parse(view.activity.resources.getString(R.string.yandex_geo_codec))
                        } catch (e: GeoParserImpl.YandexGeoCoderException) {
                            throw RuntimeException("Данные не получены, проверьте интернет соединение")
                        }
                        AddressModel.geoCoder = geoCoder
                                ?: throw  RuntimeException("Неверные данные, не возможно получить координаты")
                        view.removeFragment(view as Fragment)
                        view.switchToFragment(R.id.latLngFragment, JumpPoint())
                    }, view::showError)
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
        panelModel.spinner.set(SpinnerHandler({
            switchFragment(view as EnterPoint, it)
        }, {}, options(), view))
        panelModel.nextButton2.set(ButtonHandler({ onClick() }, R.string.home_address_btn, view))
        model.clearButton.set(ButtonHandler({ clear() }, R.string.address_clear, view))
        model.recordButton.set(ButtonHandler({ record() }, R.string.address_record, view))
        model.recordButton.get()?.visible?.set(View.INVISIBLE)
        model.onFocusLost = this.recordSoundToField()
        updateUIModel()
    }
    val REG_ID = R.id.text_region_1
    val CITY_ID = R.id.text_city_1
    val STREET_ID = R.id.text_street_1
    val HOUSE_ID = R.id.text_house_1

    class ResponseHolder (val vm:AddressPointVM):retrofit2.Callback<SoundResponse>{
        override fun onFailure(call: retrofit2.Call<SoundResponse>, t: Throwable) {
            Observable.just(true).subscribe({throw t},vm.view::showError)
        }

        override fun onResponse(call: retrofit2.Call<SoundResponse>, response: Response<SoundResponse>) {
            if (response.body()!=null) {
                val results = response.body()!!.results
                val alternatives = results.get(0).alternatives
                vm.response = alternatives.get(0).toString()
                return
            }
            val bytes = response.errorBody()?.bytes()!!
            val error = Gson().fromJson(String(bytes), ResponseError::class.java)
            vm.response = error.error.toString()
        }
    }

    var response:String? = null

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun recordSoundToField() =  FocusData(Consumer{ v ->
        focusItem(v.id, false)
        val bytes = File("/sdcard/Android/data/ru.android.zheka.gmapexample1/testAudio")
                .readBytes()
        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP) //no eol

        //TODO get content sound
        //TODO hide string key
        // TODO view.activity.?.path!!
        SoundParser(base64).parse_("AIzaSyCC4Wzcq00qH3LEXqCro9xPdLxcqTREPtQ")
                .enqueue(ResponseHolder(this))
        recordField(v.id, response?:"null")
        updateDb()
        Toast.makeText(v.context,v.id.toString(),300).show()
    }, Consumer { v ->
        focusItem(v.id, true)
        model.recordButton.get()?.visible?.set(View.VISIBLE)
    })

    private fun recordField(id: Int, msg: String) {
        when(id) {
            CITY_ID -> { model.city.set(msg) }
            STREET_ID -> { model.street.set(msg) }
            HOUSE_ID -> { model.house.set(msg) }
            REG_ID -> { model.region.set(msg) }
        }
    }

    private fun focusItem(id:Int, isFocus:Boolean) {
        when(id) {
            CITY_ID -> { model.focusCity.set(isFocus) }
            STREET_ID -> { model.focusStreet.set(isFocus) }
            HOUSE_ID -> { model.focusHouse.set(isFocus) }
            REG_ID -> { model.focusReg.set(isFocus) }
        }
    }

    private var permissions: Array<String> = arrayOf(Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private lateinit var recorder: MediaRecorder

    class RecordViewModel: ViewModel {
        public constructor()
    }

    private fun record() {
        ActivityCompat.requestPermissions(view.activity, permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        val viewModel = ViewModelProvider(view as Fragment).get(RecordViewModel::class.java)

        viewModel.viewModelScope.launch {
            delay(2000)// TODO wait while timer or click
            recorder.apply {
                stop()
                reset()
                release()
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
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setMaxDuration(30000)
            //TODO DirExternal
            setOutputFile("/sdcard/Android/data/ru.android.zheka.gmapexample1/testAudio")
            prepare()
            start()   // Recording is now started
        }
    }



    override fun clear() {
        val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config
        config.address = aDelimiter + aDelimiter + aDelimiter
        DbFunctions.add(config)
        updateUIModel()
    }

    private fun updateUIModel() {
        val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config
        if (config.tenMSTime != "0") Toast.makeText(view.context, "Определение местоположения станет помехой, лучше его отключить", 30).show()
        var address = config.address
        if (address.isNotEmpty()) {
            var endpos = address.indexOf(aDelimiter)
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
