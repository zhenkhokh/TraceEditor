package ru.android.zheka.vm

import ru.android.zheka.coreUI.IVM
import ru.android.zheka.model.IMapModel

interface IMapVM : IVM<IMapModel?> { //TODO
    fun geo()
}