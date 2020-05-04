package ru.android.zheka.vm.trace

import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.RxTransformer
import ru.android.zheka.coreUI.SpinnerHandler
import ru.android.zheka.fragment.ITrace
import ru.android.zheka.fragment.Trace
import ru.android.zheka.gmapexample1.PositionInterceptor
import ru.android.zheka.gmapexample1.PositionUtil
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.TraceActivity
import ru.android.zheka.model.IHomeModel
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.EditVM

class TraceWayPointsVM(view: ITrace, model: LatLngModel, override var panelModel: IHomeModel) : EditVM(view, model), ITraceWayPointsVM {

    override fun onClick(pos: Int) {
        Observable.just(true).compose(RxTransformer.observableIoToMain())
                .subscribe({
                    if (model.checked.filter { it }.count() > 0)
                        panelModel.nextButton2.set(ButtonHandler({ add() }, R.string.ok_choice, view))
                    else
                        panelModel.nextButton2.set(ButtonHandler())
                }, view::showError)
    }

    override val onClickListener: View.OnClickListener?
        get() = View.OnClickListener { view ->
            val isChecked = !model.checked[handler.adapterPosition]
            view.findViewById<CheckBox>(R.id.rowCheck).isChecked = isChecked
            model.checked[handler.adapterPosition] = isChecked
            onClick(handler.adapterPosition)
        }

    override fun getOptions(): List<String> {
        val res = view.activity.resources
        return listOf(res.getString(R.string.trace_spinner_load),
                res.getString(R.string.trace_spinner_start),
                res.getString(R.string.trace_spinner_way),
                res.getString(R.string.trace_spinner_end))
    }

    override fun onResume() {
        super.onResume()
        model.checked = ArrayList(shownItems.map { false }.toList())
        if (model.checked.filter { it }.count() > 0)
            panelModel.nextButton2.set(ButtonHandler({ add() }, R.string.ok_choice, view))
    }

    override fun onDestroy() {
        super.onDestroy()
        panelModel.inputVisible().set(View.GONE)
        panelModel.action().set("")
        panelModel.spinner.set(SpinnerHandler())
        panelModel.nextButton2.set(ButtonHandler())
        model.checked.clear()
        model.chekedVisibility = View.GONE
    }

    private fun add() {
        val position = PositionInterceptor(view.activity)
        position.updatePosition()
        if (TraceActivity.isOtherMode(position.state)) {
            throw RuntimeException("Подан другой режим, для сброса вернитесь в начало маршрута")
        }
        if (position.state == PositionUtil.TRACE_PLOT_STATE.END_COMMAND) {
            Toast.makeText(view.activity, "Маршрут задан, перейдите к просмотру", 15).show()
            return
        }
        if (position.state == PositionUtil.TRACE_PLOT_STATE.CONNECT_COMMAND
                || position.state == PositionUtil.TRACE_PLOT_STATE.CENTER_START_COMMAND) {
            val map = (shownItems zip model.checked)
                    .associate { Pair(it.first, it.second) }.filter { it.value }.toMap()
            val names = ArrayList(map.keys)
            val source:String
            if (position.state == PositionUtil.TRACE_PLOT_STATE.CONNECT_COMMAND) {
                source = "к промежуточным"
            } else
                source = "к старту"
            Toast.makeText(view.activity, "Путевые точки успешно добавлены $source. Добавьте конец маршрута", 15).show()
            position.setExtraPointsFromCopy(names)
            view.activity.intent = position.newIntent
            return
        }
        throw RuntimeException("Невозможно выполнить: начало маршрута не задано")
    }

    override var spinnerConsumer = Consumer<String> {
        switchFragment(Trace(), it)
    }
}