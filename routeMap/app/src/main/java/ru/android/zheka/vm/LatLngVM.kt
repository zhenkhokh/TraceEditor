package ru.android.zheka.vm

import android.content.Context
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Point
import ru.android.zheka.model.ILatLngModel
import ru.android.zheka.model.LatLngModel

class LatLngVM(override val view: IActivity, val model: LatLngModel) : ILatLngVM {
    val points : List<Point>
    init {
        points = DbFunctions.getTablesByModel(Point::class.java) as List<Point>
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
