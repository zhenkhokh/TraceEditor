package ru.android.zheka.model

import ru.android.zheka.core.IInfoModel
import ru.android.zheka.gmapexample1.MapsActivity

interface IMapModel : IInfoModel {
    var actvity: MapsActivity
    var isFakeStart: Boolean
}