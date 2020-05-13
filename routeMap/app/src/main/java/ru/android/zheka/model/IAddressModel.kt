package ru.android.zheka.model

import androidx.databinding.ObservableField
import ru.android.zheka.coreUI.ButtonHandler

interface IAddressModel : IEnterPointModel {
    var region: ObservableField<String>
    var city: ObservableField<String>
    var street: ObservableField<String>
    var house: ObservableField<String>
    var clearButton: ObservableField<ButtonHandler>
}
