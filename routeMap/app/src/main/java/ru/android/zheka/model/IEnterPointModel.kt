package ru.android.zheka.model

import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import com.google.android.gms.maps.model.LatLng
import ru.android.zheka.core.IInfoModel

interface IEnterPointModel : IInfoModel {
    companion object {
        val ADDRESS_ON: Int = 0
        val COORDINATE_ON: Int = 1
    }
    var visiability: ObservableInt
    var point: LatLng?
    var lat: ObservableField<String>
    var lng: ObservableField<String>
    var spinnerOptions: String?
}
