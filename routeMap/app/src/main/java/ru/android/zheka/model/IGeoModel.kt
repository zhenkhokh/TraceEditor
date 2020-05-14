package ru.android.zheka.model

import androidx.databinding.ObservableInt
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import ru.android.zheka.core.IInfoModel
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.db.Config
import ru.android.zheka.gmapexample1.PositionInterceptor

interface IGeoModel : IInfoModel {
    val point: LatLng
    val position: PositionInterceptor
    var activity: IActivity
    val onCameraChanged: GoogleMap.OnCameraChangeListener
    var config: Config
    var hidePanel: ObservableInt
}