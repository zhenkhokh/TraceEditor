package ru.android.zheka.vm.trace

import android.content.Intent
import android.view.View
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.coreUI.RxTransformer
import ru.android.zheka.fragment.Trace
import ru.android.zheka.gmapexample1.MapsActivity
import ru.android.zheka.gmapexample1.PositionInterceptor
import ru.android.zheka.gmapexample1.PositionUtil
import ru.android.zheka.gmapexample1.PositionUtil.LAT_LNG
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.EditVM

class TraceEndVM(view: IActivity, model: LatLngModel) : EditVM(view, model), ITraceEndVM {

    override fun onClick(pos: Int) {
        Observable.just(true).compose(RxTransformer.observableIoToMain())
                .subscribe({
                    finish(points[pos].data)
                }, view::showError)
    }

    companion object {
        var start: LatLng = LAT_LNG
    }

    fun finish(pointData: LatLng?) {
        val intent = view.activity.intent
        val positionInterceptor = PositionInterceptor(view.activity)
        view.activity.intent = positionInterceptor.updatePosition()
        positionInterceptor.end = pointData

        if (positionInterceptor.state == PositionUtil.TRACE_PLOT_STATE.CONNECT_COMMAND) {
            view.activity.intent = positionInterceptor.newIntent
            panelModel.action().set("Путевые точки заданы")
            setGoButton(positionInterceptor)
            return
        }

        if (positionInterceptor.state == PositionUtil.TRACE_PLOT_STATE.END_COMMAND) {
            panelModel.action().set("Маршрут задан, перейдите к просмотру")
            positionInterceptor.start = positionInterceptor.start ?: start
            view.activity.intent = positionInterceptor.newIntent
            setGoButton(positionInterceptor)
            return
        }

        if (positionInterceptor.state == PositionUtil.TRACE_PLOT_STATE.CENTER_START_COMMAND) {
            setCenterEndState(positionInterceptor)
            view.activity.intent = positionInterceptor.newIntent
            setGoButton(positionInterceptor)
            panelModel.action().set("Маршрут задан, но можно добавить путевые ")
            return
        }

        view.activity.intent = intent
        throw  RuntimeException("Невозможно выполнить: начало маршрута не задано")
    }

    private fun setCenterEndState(positionInterceptor: PositionInterceptor) {
        start = positionInterceptor.start
        positionInterceptor.start = null
    }

    override var spinnerConsumer = Consumer<String> {
        switchFragment(Trace(), it)
    }

    private fun setGoButton(positionInterceptor: PositionInterceptor) {
        if (canItPlot(positionInterceptor)) {
            panelModel.nextButton2.set(ButtonHandler({ goAction() }, R.string.title_activity_maps, view))
            return
        }
        panelModel.nextButton2.get()?.visible?.set(View.INVISIBLE)
        throw RuntimeException("Начало:${positionInterceptor.start} " +
                "или конец:${positionInterceptor.end} оказались не заданы. Сбросьте маршрут")
    }

    private fun canItPlot(positionInterceptor: PositionInterceptor): Boolean {
        return !(positionInterceptor.start ?: start == null ||
                (positionInterceptor.start ?: start).equals(LAT_LNG) ||
                positionInterceptor.end == null || positionInterceptor.end.equals(LAT_LNG)
                )
    }

    override fun goAction() {
        val intent = view.activity?.intent
        intent?.setClass(view.context, MapsActivity::class.java)
        intent?.setAction(Intent.ACTION_VIEW)
        view.activity?.startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        panelModel.nextButton2.set(ButtonHandler())
    }

    override fun getOptions(): List<String> {
        val res = view.activity.resources
        return listOf(res.getString(R.string.trace_spinner_load),
                res.getString(R.string.trace_spinner_start),
                res.getString(R.string.trace_spinner_way),
                res.getString(R.string.trace_spinner_end))
    }
}