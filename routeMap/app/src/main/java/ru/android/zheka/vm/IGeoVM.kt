package ru.android.zheka.vm

import ru.android.zheka.coreUI.IVM
import ru.android.zheka.model.IGeoModel

interface IGeoVM : IVM<IGeoModel?> {
    fun home()
    fun points()
    fun savePoint()
    fun pointToTrace()
    fun map()
    fun addWayPoints()
}