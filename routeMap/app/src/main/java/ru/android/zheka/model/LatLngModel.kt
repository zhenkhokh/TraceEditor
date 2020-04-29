package ru.android.zheka.model

import android.content.Context
import androidx.databinding.ObservableField
import ru.android.zheka.coreUI.PanelModel

class LatLngModel(view: Context?) : PanelModel(view), ILatLngModel {
    override var trigered: Boolean = false

    private var t = ObservableField<String>()
    override fun titleText(): ObservableField<String> {
        return t
    }
    private val _checked:HashSet<String> = hashSetOf()
    override val checked: HashSet<String>
        get() = _checked

    private var _option:String? = null
    override var spinnerOption: String
        get() = _option?:""
        set(value) {_option = value}
}