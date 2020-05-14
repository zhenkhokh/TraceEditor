package ru.android.zheka.vm

import ru.android.zheka.coreUI.IVM
import ru.android.zheka.model.IGeoModel

interface IGeoVM : IVM<IGeoModel?> {
    fun home()
    fun savePoint()
    fun map()
    fun addCPoint()
    fun goPosition()
    fun hide()
}