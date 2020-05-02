package ru.android.zheka.vm

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import ru.android.zheka.core.IInfoModel
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Trace
import ru.android.zheka.fragment.EditTraces
import ru.android.zheka.fragment.IEditTraces
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.SaveDialog
import ru.android.zheka.model.LatLngModel

class EditTracesVM(view: IEditTraces, model: LatLngModel) : EditVM(view, model), IEditTracesVM {
    val traces: List<Trace>

    init {
        traces = DbFunctions.getTablesByModel(Trace::class.java) as List<Trace>
    }

    override val shownItems: List<String>
        get() = traces.map { point -> point.name }.toList()

    override fun onClick(pos: Int) {
        if (editOptions[0].equals(model.spinnerOption)) {
            var saveDialog = TraceSaveDialog().newInstance(model.spinnerOption) as TraceSaveDialog
            saveDialog.view = view
            saveDialog.panelModel = panelModel
            saveDialog.trace = traces[pos]
            saveDialog.show(view.activity.fragmentManager, model.spinnerOption)
            return
        }
        RemoveDialog(Consumer { a -> removeTrace(pos) }, view, traces[pos].name,
                R.string.cancel_save_point).show()
    }

    private fun removeTrace(pos: Int) {
//        DbFunctions.delete(traces[pos])//TODO uncomment
    }

    override fun onResume() {
        super.onResume()
        model.titleText().set(view.activity.resources.getString(R.string.title_activity_traces))
        panelModel.action().set("Выберете действие над маршрутом и нажмите на него")
    }
}

class TraceSaveDialog : SaveDialog() {
    lateinit var trace: Trace
    lateinit var view: IActivity
    lateinit var panelModel: IInfoModel
    lateinit var editTraces: EditTraces

    override fun positiveProcess() {
        Observable.just(true).subscribe({
            val newName = nameField!!.text.toString()
            if (DbFunctions.getModelByName(newName, Trace::class.java) != null) {
                throw RuntimeException("Введеное имя уже существует, введите другое")
            }
            DbFunctions.delete(trace)
            trace.name = newName
            DbFunctions.add(trace)
            editTraces = EditTraces()
            view.switchToFragment(R.id.latLngFragment, editTraces)
        },
                view::showError)
    }

    override fun newInstance(): SaveDialog {
        isCancelable = false
        return this
    }
}
