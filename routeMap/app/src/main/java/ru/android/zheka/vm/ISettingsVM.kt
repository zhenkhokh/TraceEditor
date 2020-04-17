package ru.android.zheka.vm

import ru.android.zheka.coreUI.IVM
import ru.android.zheka.model.ISettingsModel

interface ISettingsVM : IVM<ISettingsModel?> {
    fun switchUpdateLen(checked: Boolean)
    fun optimizationNo()
    fun optimizationGoogle()
    fun optimizationBellmanFord()
    fun speedTraceControl(input:String )
    fun switchIgnorePaidRoads(checked: Boolean)
    fun switchIgnoreHighWays(checked: Boolean)
    fun switchIgnoreInDoors(checked: Boolean)
    fun switchOffline(checked: Boolean)
    fun travelMode(input: String)
    fun updateRatePositionMode(input: String)
}
