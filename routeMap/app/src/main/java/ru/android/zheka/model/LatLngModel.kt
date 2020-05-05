package ru.android.zheka.model

import android.content.Context
import android.view.View
import androidx.databinding.ObservableField
import ru.android.zheka.coreUI.PanelModel
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Point

class LatLngModel(view: Context?) : PanelModel(view), ILatLngModel {
    override var trigered: Boolean = false

    private var t = ObservableField<String>()
    override fun titleText(): ObservableField<String> {
        return t
    }

    override val _customPoints: ArrayList<Point> = ArrayList()
    override val points: List<Point>
        get() = if (_customPoints.isNotEmpty())
            _customPoints
        else
            DbFunctions.getTablesByModel(Point::class.java) as List<Point>

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