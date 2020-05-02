package ru.android.zheka.vm.trace

import io.reactivex.Observable
import ru.android.zheka.coreUI.RxTransformer
import ru.android.zheka.fragment.ITrace
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.EditVM

class TraceEndVM(view: ITrace, model : LatLngModel) : EditVM(view, model), ITraceEndVM {

    override fun onClick(pos: Int) {
        Observable.just(true).compose(RxTransformer.observableIoToMain())
                .subscribe({

                },view::showError)
    }

    override fun getOptions(): List<String> {
        val res = view.activity.resources
        return listOf(res.getString(R.string.trace_spinner_load),
            res.getString(R.string.trace_spinner_start),
            res.getString(R.string.trace_spinner_way),
            res.getString(R.string.trace_spinner_end))
    }

    // do destroy job
    override val shownItems: List<String>
        get() = points.map { point -> point.name }.toList()
}