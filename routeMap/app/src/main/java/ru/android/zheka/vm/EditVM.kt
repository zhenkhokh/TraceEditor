package ru.android.zheka.vm

import android.content.Context
import android.view.View
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Point
import ru.android.zheka.fragment.IEdit
import ru.android.zheka.fragment.LatLngHandler
import ru.android.zheka.model.EditModel
import ru.android.zheka.model.IEditModel

class EditVM(override val view: IEdit, val model: EditModel) : IEditVM {
    private val points: List<Point>

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
        get() = View.OnClickListener {view-> }//TODO


    override val shownItems: List<String>
        get() = points.map { point -> point.name }.toList()

    override val context: Context
        get() = view.context

    override fun onResume() {
    }

    override fun model(): IEditModel {
        return model
    }

    override fun onDestroy() {
    }
}
