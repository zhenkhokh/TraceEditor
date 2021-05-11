package ru.android.zheka.model

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import ru.android.zheka.core.IInfoModel
import ru.android.zheka.coreUI.SpinnerHandler

interface ISettingsModel : IInfoModel {
    fun updateLen(): ObservableBoolean
    fun optimizationNo(): ObservableBoolean
    fun optimizationGoogle(): ObservableBoolean
    fun optimizationBellmanFord(): ObservableBoolean
    val avoidHighWays: ObservableBoolean
    val avoidInDoor: ObservableBoolean
    val avoid: ObservableBoolean
    val offline: ObservableBoolean
    val spinnerTimer: ObservableField<SpinnerHandler?>
    val spinnerTravel: ObservableField<SpinnerHandler?>
    val posTimer: ObservableInt
    val posTravel: ObservableInt
    val speedTrace: ObservableInt
}