package ru.android.zheka.model

import android.content.Context
import ru.android.zheka.coreUI.PanelModel
import ru.android.zheka.gmapexample1.MapsActivity

class MapModel(view: Context?) : PanelModel(view), IMapModel {
    override lateinit var actvity: MapsActivity

}