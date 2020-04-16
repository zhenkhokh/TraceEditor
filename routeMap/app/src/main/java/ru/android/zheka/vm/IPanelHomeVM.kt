package ru.android.zheka.vm

import ru.android.zheka.coreUI.IVM
import ru.android.zheka.model.IHomeModel

interface IPanelHomeVM : IVM<IHomeModel?> {
    fun info()
    fun address()
    fun geo()
    fun editPoints()
    fun editTraces()
    fun pointNavigate()
    fun createTrace()
}