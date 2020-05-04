package ru.android.zheka.vm.trace

import android.content.Intent
import com.google.android.gms.maps.model.LatLng
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
                    if (resetAndStartTrace(PositionInterceptor(view.activity), points[pos])
                            != PositionUtil.TRACE_PLOT_STATE.CENTER_START_COMMAND)
                        panelModel.action().set("Начало маршрута задано")

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
                }, view::showError)
    }

    fun resetAndStartTrace(positionInterceptor: PositionInterceptor, point: Point): PositionUtil.TRACE_PLOT_STATE? {
        val pointData: LatLng = point.data
        positionInterceptor.centerPosition = pointData
        positionInterceptor.start = pointData
        val intent = positionInterceptor.updatePosition()
        view.activity.intent = intent
        val state = positionInterceptor.state
        if (state == PositionUtil.TRACE_PLOT_STATE.END_COMMAND) {
            val dialog = StartAskDialog.Builder()
                    .point(point).vm(this).positionInterceptor(positionInterceptor).build()
            dialog.show(view.activity.fragmentManager, "Message")
            return state
        }
        if (state == PositionUtil.TRACE_PLOT_STATE.CENTER_START_COMMAND
                || state == PositionUtil.TRACE_PLOT_STATE.CONNECT_COMMAND) {
            resetTrace()
            view.activity.intent = intent
            panelModel.action().set("Переопределение старта и сброс машрута")
        }
        view.activity.intent = positionInterceptor.newIntent
        return state
    }

    fun resetTrace() {
        view.activity.intent = Intent()
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

    // do destroy job
    override val shownItems: List<String>
        get() = points.map { point -> point.name }.toList()
}

class StartAskDialog : SingleChoiceDialog("") {
    class Builder {
        private val dialog: StartAskDialog = StartAskDialog()
        fun vm(vm: TraceStartVM): Builder {
            StartAskDialog.vm = vm
            return this
        }

        fun point(p: Point): Builder {
            point = p
            return this
        }

        fun positionInterceptor(p: PositionInterceptor): Builder {
            positionInterceptor = p
            return this
        }

        fun build(): StartAskDialog {
            return this.dialog
        }
    }

    companion object {
        lateinit var vm: TraceStartVM
        lateinit var point: Point
        lateinit var positionInterceptor: PositionInterceptor
    }

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
        vm.resetTrace()
        vm.resetAndStartTrace(positionInterceptor, point)
        vm.panelModel.action().set("Начало маршрута задано")
    }
}