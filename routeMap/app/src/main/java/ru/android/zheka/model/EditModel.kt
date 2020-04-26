package ru.android.zheka.model

import android.content.Context
import androidx.databinding.ObservableField
import ru.android.zheka.coreUI.PanelModel

class EditModel(view: Context) :    PanelModel(view), IEditModel {
    val _t = ObservableField<String>()
    override val titleText: ObservableField<String>
        get() = _t
}
