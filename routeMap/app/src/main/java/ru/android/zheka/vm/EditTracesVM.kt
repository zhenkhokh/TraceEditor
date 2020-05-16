package ru.android.zheka.vm

import io.reactivex.Observable
import io.reactivex.functions.Consumer
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.coreUI.SaveDialog
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Trace
import ru.android.zheka.fragment.EditTraces
import ru.android.zheka.fragment.IEditTraces
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.LatLngModel

class EditTracesVM(view: IEditTraces, model: LatLngModel) : EditVM(view, model), IEditTracesVM {
    val traces: MutableList<Trace>

    init {
        traces = DbFunctions.getTablesByModel(Trace::class.java) as MutableList<Trace>
    }

    override val shownItems: MutableList<String>
        get() = traces.map { trace -> trace.name }.toMutableList()

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
        DbFunctions.delete(traces[pos])
        view.switchToFragment(R.id.latLngFragment, EditTraces())
    }

    override fun onResume() {
        super.onResume()
        model.titleText().set(view.activity.resources.getString(R.string.title_activity_traces))
        panelModel.action().set(view.activity.resources.getString(R.string.action_traces))
    }

    override fun onDestroy() {
        super.onDestroy()
        traces.removeAll(traces)
    }
}

class TraceSaveDialog : SaveDialog() {
    lateinit var trace: Trace
    lateinit var view: IActivity
    lateinit var panelModel: IPanelModel
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
