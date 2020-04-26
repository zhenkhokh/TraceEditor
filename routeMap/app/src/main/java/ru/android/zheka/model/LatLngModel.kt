package ru.android.zheka.model

import android.content.Context
import androidx.databinding.ObservableField
import ru.android.zheka.coreUI.PanelModel

class LatLngModel(view: Context) : PanelModel(view), ILatLngModel {
    private val _t = ObservableField<String>()
    override val titleText: ObservableField<String>
        get() = _t
}
