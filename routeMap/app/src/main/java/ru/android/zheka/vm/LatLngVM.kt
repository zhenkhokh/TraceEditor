package ru.android.zheka.vm

import android.content.Context
import android.view.View
import android.widget.Toast
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Point
import ru.android.zheka.fragment.LatLngHandler
import ru.android.zheka.model.ILatLngModel
import ru.android.zheka.model.LatLngModel

class LatLngVM(override val view: IActivity, val model: LatLngModel) : ILatLngVM {
    val points: List<Point>

    init {
        points = DbFunctions.getTablesByModel(Point::class.java) as List<Point>
    }

    private lateinit var _handler: LatLngHandler
    override var handler: LatLngHandler
        get() = _handler
        set(value) {
            _handler = value
        }

    override val onClickListener: View.OnClickListener?
        get() = View.OnClickListener { view -> onClick(_handler.adapterPosition) }

    private fun onClick(adapterPosition: Int) {
        Toast.makeText(view.context,""+adapterPosition,15).show()
    }

    override val shownItems: List<String>
        get() = points.map { point -> point.name }.toList()

    override val context: Context
        get() = view.context

    override fun onResume() {
    }

    override fun model(): ILatLngModel {
        return model
    }

    override fun onDestroy() {
    }
}
