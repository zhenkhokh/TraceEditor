package ru.android.zheka.vm

import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Trace
import ru.android.zheka.fragment.IEditTraces
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.LatLngModel

class EditTracesVM(view: IEditTraces, model: LatLngModel) : EditVM(view, model), IEditTracesVM {
    val traces:List<Trace>
    init {
        traces = DbFunctions.getTablesByModel(Trace::class.java) as List<Trace>
    }

    override val shownItems: List<String>
        get() = traces.map { point -> point.name }.toList()

    override fun onClick(pos:Int) {

    }

    override fun onResume() {
        super.onResume()
        model.titleText.set(view.activity.resources.getString(R.string.title_activity_traces))
        panelModel.action().set("Выберете действие над маршрутом и нажмите на него")
    }
}