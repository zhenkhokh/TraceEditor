package ru.android.zheka.vm.trace

import android.content.Intent
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.functions.Consumer
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.RxTransformer
import ru.android.zheka.fragment.ITrace
import ru.android.zheka.fragment.Trace
import ru.android.zheka.gmapexample1.MapsActivity
import ru.android.zheka.gmapexample1.PositionInterceptor
import ru.android.zheka.gmapexample1.PositionUtil
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.EditVM
import java.lang.RuntimeException

class TraceEndVM(view: ITrace, model: LatLngModel) : EditVM(view, model), ITraceEndVM {

    override fun onClick(pos: Int) {
        Observable.just(true).compose(RxTransformer.observableIoToMain())
                .subscribe({
                    val positionInterceptor = PositionInterceptor(view.activity)
                    val intent = view.activity.intent
                    view.activity.intent = positionInterceptor.updatePosition()

                    if (positionInterceptor.state == PositionUtil.TRACE_PLOT_STATE.CENTER_START_COMMAND) {
                        positionInterceptor.end = points[pos].data
                        positionInterceptor.start = null
                        view.activity.intent = positionInterceptor.newIntent
                        setGoButton()
                        panelModel.action().set("Маршрут задан, перейдите к просмотру")
                        return@subscribe
                    }

                    if (positionInterceptor.state == PositionUtil.TRACE_PLOT_STATE.END_COMMAND) {
                        Toast.makeText(view.context, "Маршрут задан, перейдите к просмотру", 15).show()
                        setGoButton()
                        return@subscribe
                    }
                    if (positionInterceptor.state == PositionUtil.TRACE_PLOT_STATE.CONNECT_COMMAND) {
                        Toast.makeText(view.context, "Промежуточные заданы", 15).show()
                        return@subscribe
                    }
                    view.activity.intent = intent
                    throw  RuntimeException("Невозможно выполнить: начало маршрута не задано")
                }, view::showError)
    }

    override var spinnerConsumer = Consumer<String> {
        switchFragment(Trace(), it)
    }

    private fun setGoButton() {
        panelModel.nextButton2.set(ButtonHandler({goAction()}, R.string.title_activity_maps, view))
    }

    private fun goAction() {
        val intent = view.activity.intent
        intent.setClass(view.context, MapsActivity::class.java)
        intent.setAction(Intent.ACTION_VIEW)
        view.activity.startActivity(intent)
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