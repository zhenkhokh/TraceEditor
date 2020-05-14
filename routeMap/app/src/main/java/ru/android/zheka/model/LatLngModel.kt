package ru.android.zheka.model

import android.content.Context
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import ru.android.zheka.coreUI.PanelModel
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Point

class LatLngModel(view: Context?) : PanelModel(view), ILatLngModel {
    private val _hideTitle = ObservableInt(View.VISIBLE)
    private val t = ObservableField<String>()
    override fun titleText(): ObservableField<String> {
        return t
    }

    override var hideTitle: ObservableInt
        get() = _hideTitle
        set(value) {}
    override var custom: Boolean = false
    override val _customPoints: MutableList<Point> = mutableListOf()
    override val points: MutableList<Point>
        get() = if (custom)
            _customPoints
        else
            DbFunctions.getTablesByModel(Point::class.java) as MutableList<Point>

    private var _chekedVisibility: Int = View.GONE
    override var chekedVisibility: Int
        get() = _chekedVisibility
        set(value) {
            _chekedVisibility = value
        }

    private var _checked: ArrayList<Boolean> = ArrayList()
    override var checked: ArrayList<Boolean>
        get() = _checked
        set(value) {
            _checked = value
        }

    private var _option: String? = null
    override var spinnerOption: String
        get() = _option ?: ""
        set(value) {
            _option = value
        }
}