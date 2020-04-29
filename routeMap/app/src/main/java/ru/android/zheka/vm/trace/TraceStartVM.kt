package ru.android.zheka.vm.trace

import android.content.Intent
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import ru.android.zheka.coreUI.RxTransformer
import ru.android.zheka.db.Point
import ru.android.zheka.fragment.ITrace
import ru.android.zheka.fragment.Trace
import ru.android.zheka.gmapexample1.MapsActivity
import ru.android.zheka.gmapexample1.PositionInterceptor
import ru.android.zheka.gmapexample1.PositionUtil.TRACE_PLOT_STATE
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.SingleChoiceDialog
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.EditVM

class TraceStartVM(view: ITrace, model: LatLngModel) : EditVM(view, model), ITraceStartVM {

    override fun onClick(pos: Int) {
        Observable.just(true).compose(RxTransformer.observableIoToMain())
                .subscribe({
                    var positionInterceptor = PositionInterceptor(view.activity)
                    var isStartCmd = false
                    try {
                        positionInterceptor.positioning()
                    } catch (e: Exception) {
                        isStartCmd = true
                        println("TraceStartVM.onclick: no center")
                    }
                    val state = positionInterceptor.state
                    if (state == TRACE_PLOT_STATE.CENTER_END_COMMAND) {
                        val dialog = ShowAskDialog()
                        dialog.vm = this
                        dialog.show(view.activity.fragmentManager, "Message")
                    }
                    if (state == TRACE_PLOT_STATE.DONOTHING_COMMAND) {
                        panelModel.success().set("Переопределение старта")
                    }
                    resetAndStartTrace(positionInterceptor, points[pos])
                }, view::showError)
    }

    fun resetAndStartTrace(positionInterceptor: PositionInterceptor, point: Point) {
        positionInterceptor.centerPosition = point.data
        positionInterceptor.end = positionInterceptor.end ?: positionInterceptor.centerPosition
        view.activity.intent = positionInterceptor.updatePosition()
        positionInterceptor.positioning()
        positionInterceptor.state = TRACE_PLOT_STATE.CENTER_START_COMMAND
        view.activity.intent = positionInterceptor.newIntent
        panelModel.action().set("Начало маршрута задано, перейдите к концу")
    }

    override fun getOptions(): List<String> {
        val res = view.activity.resources
        return listOf(res.getString(R.string.trace_spinner_load),
                res.getString(R.string.trace_spinner_start),
                res.getString(R.string.trace_spinner_way),
                res.getString(R.string.trace_spinner_end))
    }

    override var spinnerConsumer = Consumer<String>{
        switchFragment(Trace(), it)
    }
}

class ShowAskDialog() : SingleChoiceDialog("") {
    lateinit var vm: TraceStartVM
    lateinit var point: Point
    lateinit var positionInterceptor: PositionInterceptor

    init {
        negativeId = R.string.cancel_plot_trace
        positiveId = R.string.ok_plot_trace
        msg = "Маршрут задан, перейти к просмотру ? Нет - сбросит маршрут"
    }

    override fun positiveProcess() {
        val intent = vm.view.activity.intent
        intent.setAction(Intent.ACTION_VIEW)
        intent.setClass(vm.context, MapsActivity::class.java)
        vm.view.activity.startActivity(intent)
    }

    override fun negativeProcess() {
        vm.resetAndStartTrace(positionInterceptor, point)
    }
}