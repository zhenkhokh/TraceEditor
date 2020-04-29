package ru.android.zheka.vm.trace

import android.content.Intent
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import ru.android.zheka.coreUI.RxTransformer
import ru.android.zheka.db.Point
import ru.android.zheka.fragment.ITrace
import ru.android.zheka.fragment.Trace
import ru.android.zheka.gmapexample1.*
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.EditVM

class TraceStartVM(view: ITrace, model: LatLngModel) : EditVM(view, model), ITraceStartVM {

    override fun onClick(pos: Int) {
        Observable.just(true).compose(RxTransformer.observableIoToMain())
                .subscribe(marker@{
                    var positionInterceptor = PositionInterceptor(view.activity)
                    if (resetAndStartTrace(positionInterceptor, points[pos])
                            != PositionUtil.TRACE_PLOT_STATE.CENTER_START_COMMAND)
                        panelModel.action().set("Начало маршрута задано")
                    view.activity.intent = positionInterceptor.newIntent

//                    val intent: Intent = view.activity.intent
//                    val positionUtil = PositionUtil()
//                    val center: LatLng?
//                    center = try {
//                        positionUtil.positionAndBoundInit(intent)
//                        positionUtil.center
//                    } catch (e: Exception) {
//                        try {
//                            positionUtil.setCenterPosition(intent)
//                            positionUtil.center
//                        } catch (ee: Exception) {
//                            //Toast.makeText(this, "center point is not specified: задайте местоположение" , 15).show();
//                            val dialog = AlertDialog("center point is not specified: задайте местоположение. "+ee.message)
//                            dialog.show(view.activity.fragmentManager, "Cooбщение")
//                            return@marker
//                        }
//                    }
//                    var isStartCmd = false
//                    var isEndCmd = false
//                    var state: TRACE_PLOT_STATE? = null
//                    try {
//                        positionUtil.positionAndBoundInit(intent)
//                    } catch (e: Exception) {
//                        isStartCmd = true
//                    }
//                    state = positionUtil.defCommand()
//                    if (isStartCmd == false && TraceActivity.isOtherMode(state)) {
//                        //Toast.makeText(this, "Подан другой режим, для сброса вернитесь в начало маршрута " + val, 30).show();
//                        val dialog = AlertDialog("Подан другой режим, для сброса вернитесь в начало маршрута")
//                        dialog.show(view.activity.fragmentManager, "Ошибка")
//                        return@marker
//                    }
//                    if (state == TRACE_PLOT_STATE.CENTER_END_COMMAND) {
//                        Toast.makeText(context, "Маршрут задан, перейдите к просмотру", 15).show()
//                        return@marker
//                    }
//                    if (state == TRACE_PLOT_STATE.CENTER_COMMAND) //<-- reset command
//                        isStartCmd = true
//                    if (state == TRACE_PLOT_STATE.CENTER_START_COMMAND) isEndCmd = true
//                    if (isStartCmd) {
//                        Toast.makeText(context, "Начало маршрута задано, перейдите к концу", 15).show()
//                        positionUtil.titleMarker = "Start"
//                        positionUtil.start = center
//                        positionUtil.end = center
//                        positionUtil.setCommand(TRACE_PLOT_STATE.CENTER_START_COMMAND)
//                        view.activity.intent = positionUtil.getIntent()
//                    }
//                    if (state == TRACE_PLOT_STATE.DONOTHING_COMMAND) {
//                        panelModel.action().set("Переопределение старта")
//                    }
                }, view::showError)
    }

    fun resetAndStartTrace(positionInterceptor: PositionInterceptor, point: Point): PositionUtil.TRACE_PLOT_STATE?{
        val pointData =  point.data
        positionInterceptor.centerPosition = point.data
        positionInterceptor.start = pointData
        positionInterceptor.end = pointData
        view.activity.intent = positionInterceptor.updatePosition()
        var state = positionInterceptor.state
        if (state == PositionUtil.TRACE_PLOT_STATE.CENTER_END_COMMAND) {
            val dialog = ShowAskDialog()
            dialog.vm = this
            dialog.show(view.activity.fragmentManager, "Message")
        }
        if (state == PositionUtil.TRACE_PLOT_STATE.CENTER_START_COMMAND) {
            panelModel.action().set("Переопределение старта")
        }
        positionInterceptor.end = null
        positionInterceptor.positioning()
        return state
    }

    override fun getOptions(): List<String> {
        val res = view.activity.resources
        return listOf(res.getString(R.string.trace_spinner_load),
                res.getString(R.string.trace_spinner_start),
                res.getString(R.string.trace_spinner_way),
                res.getString(R.string.trace_spinner_end))
    }

    override var spinnerConsumer = Consumer<String> {
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
        vm.view.activity.intent = positionInterceptor.newIntent
        vm.panelModel.action().set("Начало маршрута задано")
    }
}