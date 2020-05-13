package ru.android.zheka.vm.jump

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.coreUI.RxTransformer
import ru.android.zheka.coreUI.SpinnerHandler
import ru.android.zheka.db.Config
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.fragment.EnterPoint
import ru.android.zheka.fragment.IEnterPoint
import ru.android.zheka.fragment.JumpPoint
import ru.android.zheka.geo.GeoParserImpl
import ru.android.zheka.gmapexample1.AddressActivity.Companion.aDelimiter
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.AddressModel
import ru.android.zheka.model.IAddressModel
import ru.android.zheka.model.IEnterPointModel
import java.lang.Exception

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
        updateUIModel()
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
