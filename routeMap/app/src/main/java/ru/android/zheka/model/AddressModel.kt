package ru.android.zheka.model

import android.content.Context
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.google.android.gms.maps.model.LatLng
import ru.android.zheka.coreUI.PanelModel

class AddressModel(context: Context) : PanelModel(context), IAddressModel {
    override var visiability: ObservableInt = ObservableInt(IEnterPointModel.COORDINATE_ON)
    override var point: LatLng?
        get() {return Companion.point}
        set(value) {Companion.point = value}

    companion object {
        var point: LatLng? = null //TODO move to AppModule
        var spinnerOptions: String? = null
    }

    private var _lng = ObservableField(point?.longitude?.toString() ?: "")
    private var _lat = ObservableField(point?.latitude?.toString() ?: "")
    override var lat: ObservableField<String>
        get() {
//        _lat.set(point?.latitude?.toString() ?: "")
            return _lat
        }
        set(value) {}

    override var lng: ObservableField<String>
        get() {
//        _lng.set(point?.longitude?.toString() ?: "")
            return _lng
        }
        set(value) {}

    override var spinnerOptions: String?
            get() = Companion.spinnerOptions
            set(value) {}
}