package ru.android.zheka.model

import android.content.Context
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.google.android.gms.maps.model.LatLng
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.OnFocusCallback
import ru.android.zheka.coreUI.PanelModel
import ru.android.zheka.geo.GeoCoder

class AddressModel(context: Context) : PanelModel(context), IAddressModel {

    private val _focusStreet: ObservableBoolean = ObservableBoolean(false)
    private val _focusHouse: ObservableBoolean = ObservableBoolean(false)
    private val _focusCity: ObservableBoolean = ObservableBoolean(false)
    private val _focusReg: ObservableBoolean = ObservableBoolean(false)

    private lateinit var _onFocusLost: OnFocusCallback
    private var _clearButton =  ObservableField(ButtonHandler())
    private var _recordButton =  ObservableField(ButtonHandler())
    private var _house: ObservableField<String> = ObservableField("")
    private var _street: ObservableField<String> = ObservableField("")
    private var _city: ObservableField<String> = ObservableField("")
    private var _region: ObservableField<String> = ObservableField("")

    override var onFocusLost: OnFocusCallback
        get() = _onFocusLost;
        set(value) {_onFocusLost = value}
    override var focusReg: ObservableBoolean
        get() = _focusReg
        set(value) {}
    override val focusCity: ObservableBoolean
        get() = _focusCity
    override val focusStreet: ObservableBoolean
        get() = _focusStreet
    override val focusHouse: ObservableBoolean
        get() = _focusHouse

    override var recordButton: ObservableField<ButtonHandler>
        get() = _recordButton
        set(value) {}
    override val region: ObservableField<String>
        get() = _region
    override val city: ObservableField<String>
        get() = _city
    override val street: ObservableField<String>
        get() = _street
    override val house: ObservableField<String>
        get() = _house
    override var clearButton: ObservableField<ButtonHandler>
        get() = _clearButton
        set(value) {}

    override var visiability: ObservableInt = ObservableInt(IEnterPointModel.COORDINATE_ON)
    override var point: LatLng?
        get() {return Companion.point}
        set(value) {Companion.point = value}

    companion object {
        lateinit var geoCoder: GeoCoder
        var point: LatLng? = null
        var spinnerOptions: String? = null
        const val aDelimiter = "#"
    }

    private var _lng = ObservableField(point?.longitude?.toString() ?: "")
    private var _lat = ObservableField(point?.latitude?.toString() ?: "")
    override var lat: ObservableField<String>
        get() {
            return _lat
        }
        set(value) {}

    override var lng: ObservableField<String>
        get() {
            return _lng
        }
        set(value) {}

    override var spinnerOptions: String?
            get() = Companion.spinnerOptions
            set(value) {}
}