package ru.android.zheka.model

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.coreUI.PanelModel
import ru.android.zheka.db.Config
import ru.android.zheka.gmapexample1.PositionInterceptor
import ru.android.zheka.gmapexample1.R

class GeoModel(view: Context?) : PanelModel(view), IGeoModel {
    override val point: LatLng
        get() = position.centerPosition

    private var position_: PositionInterceptor? = null
    override val position: PositionInterceptor
        get() = position_!!
    private lateinit var activity_: IActivity
    override var activity: IActivity
        get() = activity_
        set(value) {
            activity_ = value
            position_ = PositionInterceptor(activity_.activity, R.id.coordinateText)
        }

    override lateinit var config: Config

    override val onCameraChanged = GoogleMap.OnCameraChangeListener {
        if (position_ != null && position.markerCenter != null ) {
            this.position.markerCenter.position = it.target
            this.position.zoom = it.zoom
            this.position.centerPosition = it.target
            if (config.uLocation) this.position.updateUILocation()
        }
    }
}