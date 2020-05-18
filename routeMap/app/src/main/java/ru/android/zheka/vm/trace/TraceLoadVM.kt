package ru.android.zheka.vm.trace

import android.content.Intent
import io.reactivex.functions.Consumer
import ru.android.zheka.db.Config
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.DbFunctions.DEFAULT_CONFIG_NAME
import ru.android.zheka.db.DbFunctions.getModelByName
import ru.android.zheka.db.Trace
import ru.android.zheka.db.UtileTracePointsSerializer
import ru.android.zheka.fragment.ITrace
import ru.android.zheka.gmapexample1.MapsActivity
import ru.android.zheka.gmapexample1.MapsActivity.Companion.updateOfflineState
import ru.android.zheka.gmapexample1.PositionInterceptor
import ru.android.zheka.gmapexample1.PositionUtil
import ru.android.zheka.gmapexample1.PositionUtil.TRACE_PLOT_STATE
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.EditVM

class TraceLoadVM(view: ITrace, model: LatLngModel) : EditVM(view, model), ITraceLoadVM {
    val traces: MutableList<Trace>

    init {
        val config = getModelByName(DEFAULT_CONFIG_NAME
                , Config::class.java) as Config
        val isOffline = config.offline.toBoolean()
        traces = DbFunctions.getTablesByModel(Trace::class.java)
                ?.filter { (isOffline && (it as Trace).data != null) || !isOffline }?.toMutableList()
                as MutableList<Trace>
    }

    override val shownItems: MutableList<String>
        get() = if (model.custom)
            mutableListOf()
        else
            traces.map { point -> point.name }.toMutableList()

    override fun onClick(pos: Int) {
        val trace = traces[pos]
        val utilTrace = UtileTracePointsSerializer()
        val position = PositionInterceptor(view.activity)
        position.start = trace.start
        position.end = trace.end
        position.centerPosition = trace.end
        val extraPoints = trace.data?.extraPoints
                ?: ArrayList()//arrayListOf(UtilePointSerializer().serialize(position.end) as String)
        position.setExtraPointsFromCopy(extraPoints)
        position.state = TRACE_PLOT_STATE.END_COMMAND
        view.activity.intent.putStringArrayListExtra(PositionUtil.EXTRA_POINTS, extraPoints)
        updateOfflineState(view.context)
        if (MapsActivity.isOffline) position.title = utilTrace.serialize(trace.data) as String
        PositionUtil.isCenterAddedToTrace = false
        val intent = position.newIntent
        intent.setClass(context, MapsActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        view.activity.startActivity(intent)
        view.activity.finish()
    }

    override fun onResume() {
        super.onResume()
        panelModel.action().set(view.activity.resources.getString(R.string.action_traces))
        model.titleText().set(view.activity.resources.getString(R.string.title_activity_traces))
    }

    override var spinnerConsumer = Consumer<String> {
        switchFragment(ru.android.zheka.fragment.Trace(), it)
    }

    override fun getOptions(): List<String> {
        val res = view.activity.resources
        return listOf(res.getString(R.string.trace_spinner_load),
                res.getString(R.string.trace_spinner_start),
                res.getString(R.string.trace_spinner_way),
                res.getString(R.string.trace_spinner_end))
    }
}