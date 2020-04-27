package ru.android.zheka.model

import android.content.Context
import androidx.databinding.ObservableField
import ru.android.zheka.coreUI.PanelModel

class LatLngModel(view: Context?) : PanelModel(view), ILatLngModel {
    var t = ObservableField<String>()
    override fun titleText(): ObservableField<String> {
        return t
    }
}