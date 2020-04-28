package ru.android.zheka.vm

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import ru.android.zheka.core.IInfoModel
import ru.android.zheka.coreUI.*
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Point
import ru.android.zheka.fragment.Edit
import ru.android.zheka.fragment.LatLngHandler
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.SaveDialog
import ru.android.zheka.model.ILatLngModel
import ru.android.zheka.model.LatLngModel

open class EditVM(override val view: IActivity, val model: LatLngModel) : IEditVM {
    protected lateinit var editOptions: List<String>
    private val points: List<Point>
    private lateinit var _pM: IInfoModel
    override var panelModel: IInfoModel
        get() = _pM
        set(value) {
            _pM = value
        }
    private lateinit var _handler: LatLngHandler

    override var handler: LatLngHandler
        get() = _handler
        set(value) {
            _handler = value
        }

    init {
        points = DbFunctions.getTablesByModel(Point::class.java) as List<Point>
    }

    override val onClickListener: View.OnClickListener?
        get() = View.OnClickListener { view -> onClick(_handler.adapterPosition) }

    open fun onClick(pos: Int) {
        if (editOptions[0].equals(model.spinnerOption)) {
            var saveDialog = PointSaveDialog().newInstance(model.spinnerOption) as PointSaveDialog
            saveDialog.view = view
            saveDialog.panelModel = _pM
            saveDialog.point = points[pos]
            saveDialog.show(view.activity.fragmentManager, model.spinnerOption)
            return
        }
        RemoveDialog(Consumer { a -> removePoint(pos) }, view, points[pos].name,
                R.string.cancel_save_point).show()
    }

    override val shownItems: List<String>
        get() = points.map { point -> point.name }.toList()

    override val context: Context
        get() = view.context

    override fun onResume() {
        editOptions = context.resources.getStringArray(R.array.editOptions).asList()
        editOptions = reOrder(editOptions, model.spinnerOption)
        model.titleText().set(view.activity.resources.getString(R.string.title_activity_points))
        panelModel.inputVisible().set(IPanelModel.COMBO_BOX_VISIBLE)
        panelModel.action().set("Выберете действие над точкой и нажмите на нее")
        panelModel.spinner.set(SpinnerHandler(Consumer { model.spinnerOption = it }, Consumer { a -> },
                editOptions, view))
    }

    private fun reOrder(editOptions: List<String>, spinnerOption: String): List<String> {
        if (spinnerOption.isNotEmpty()) {
            val i = editOptions.lastIndexOf(spinnerOption)
            if (i != -1) {
                val out = ArrayList(editOptions.subList(i, editOptions.size))
                if (out.addAll(editOptions.subList(0, i)))
                    return out
            }
        }
        return editOptions
    }

    private fun removePoint(adapterPosition: Int) {
//        DbFunctions.delete(points[adapterPosition])//TODO uncomment
    }

    override fun model(): ILatLngModel {
        return model
    }

    override fun onDestroy() {
        panelModel.inputVisible().set(View.GONE)
        panelModel.action().set("")
    }

    class RemoveDialog(var consumer: Consumer<Boolean>,
                       val view: IActivity, val value: String,
                       val idNegBtn: Int) : ErrorDialog(DialogConfig.builder()
            .contentValue(value)
            .context(view.getContext())
            .labelValue(view.activity.resources.getString(R.string.points_column_name1))
            .positiveConsumer(consumer)
            .layoutId(R.layout.dialog_error)
            .titleId(R.id.errorDialog_windowTitle)
            .contentId(R.id.errorDialog_value)
            .poistiveBtnId(R.string.ok_save_point).build(),
            view) {

        override fun configureDialog(view_: View): AlertDialog {
            getContent(view_)
            return AlertDialog.Builder(config.context)
                    .setView(view_)
                    .setPositiveButton(config.poistiveBtnId) { d, _ ->
                        d.cancel()
                        Observable.just(true).subscribe(config.positiveConsumer,
                                Consumer { view.showError(it) }).dispose();
                    }
                    .setNegativeButton(idNegBtn) { d, _ -> d.cancel() }
                    .create()
        }
    }

    class PointSaveDialog : SaveDialog() {
        lateinit var point: Point
        lateinit var view: IActivity
        lateinit var panelModel: IInfoModel

        override fun positiveProcess() {
            Observable.just(true).subscribe({
                val newName = nameField!!.text.toString()
                if (DbFunctions.getModelByName(newName, Point::class.java) != null) {
                    throw RuntimeException("Введеное имя уже существует, введите другое")
                }
                DbFunctions.delete(point)
                point.name = newName
                DbFunctions.add(point)
                val fragment = Edit()
                fragment.panelModel = panelModel
                view.switchToFragment(R.id.latLngFragment, fragment)
            },
                    view::showError)
        }

        override fun newInstance(): SaveDialog {
            isCancelable = false
            return this
        }
    }
}


