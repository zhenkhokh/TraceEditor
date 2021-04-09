package ru.android.zheka.vm.jump

import android.view.View
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.coreUI.RxTransformer
import ru.android.zheka.coreUI.SpinnerHandler
import ru.android.zheka.fragment.EnterPoint
import ru.android.zheka.fragment.IEnterPoint
import ru.android.zheka.gmapexample1.GeoPositionActivity
import ru.android.zheka.gmapexample1.PositionInterceptor
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.AddressModel
import ru.android.zheka.model.IAddressModel
import ru.android.zheka.model.IEnterPointModel

class CoordinatePointVM(val view: IEnterPoint, val model: IEnterPointModel) : ICoordinatePointVM {

    override lateinit var panelModel: IPanelModel

    override fun onResume() {
        panelModel.action().set("Используйте десятичный формат чисел")
        panelModel.inputVisible().set(IPanelModel.COMBO_BOX_VISIBLE)
        panelModel.spinner.set(SpinnerHandler({
            switchFragment(view as EnterPoint, it)
        }, {}, options(), view))
        panelModel.nextButton2.set(ButtonHandler({ onClick() }, R.string.home_address_btn, view))
    }

    override fun onClick() {
        Observable.just(true).compose(RxTransformer.observableIoToMain())
                .subscribe({
                    val lat: Double
                    try {
                        lat = model.lat.get()!!.toDouble()
                    } catch (e: Exception) {
                        throw RuntimeException("Неверный формат широты")
                    }
                    val lng: Double
                    try {
                        lng = model.lng.get()!!.toDouble()
                    } catch (e: Exception) {
                        throw RuntimeException("Неверный формат долготы")
                    }
                    model.point = LatLng(lat, lng)
                    val positionInterceptor = PositionInterceptor(view.activity)
                    positionInterceptor.centerPosition = model.point
                    val geoIntent = positionInterceptor.newIntent
                    geoIntent.setClass(view.context, GeoPositionActivity::class.java)

                    view.activity.startActivity(geoIntent)
                    view.activity.finish()
                },
                        view::showError)
    }

    override fun options(): List<String> {
        val res = view.activity.resources
        return listOf<String>(res.getString(R.string.enter_point_coordinate),
                res.getString(R.string.enter_point_address),
                res.getString(R.string.enter_point_address_en))
    }

    override fun model(): IAddressModel {
        return model as IAddressModel
    }

    override fun onDestroy() {
        panelModel.nextButton2.get()?.visible?.set(View.GONE)
        panelModel.inputVisible().set(View.GONE)
        panelModel.action().set("")
    }

    override fun switchFragment(fragment: EnterPoint, option: String) {
        val triggered = model.spinnerOptions == option
        AddressModel.spinnerOptions = option
        if (!triggered) {
            view.switchToFragment(R.id.latLngFragment, EnterPoint())
        }
    }
}
