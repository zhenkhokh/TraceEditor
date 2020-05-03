package ru.android.zheka.fragment

import android.content.res.Resources
import android.view.View
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.databinding.LatLngFragmentBinding
import ru.android.zheka.model.IHomeModel
import ru.android.zheka.model.ILatLngModel
import ru.android.zheka.vm.IEditVM
import ru.android.zheka.vm.trace.TraceEndVM
import ru.android.zheka.vm.trace.TraceLoadVM
import ru.android.zheka.vm.trace.TraceStartVM
import ru.android.zheka.vm.trace.TraceWayPointsVM
import javax.inject.Inject

class Trace : Edit(), ITrace {
    @Inject
    lateinit var viewModelLoad: TraceLoadVM

    @Inject
    lateinit var viewModelWayPoints: TraceWayPointsVM

    @Inject
    lateinit var viewModelStart: TraceStartVM

    @Inject
    lateinit var viewModelEnd: TraceEndVM

    @Inject
    override lateinit var panelModel: IHomeModel

    override fun initAdapter(binding: LatLngFragmentBinding): LatLngFragmentBinding {
        viewModel = defineVM(viewModel.model(), viewModelLoad.view.activity.resources)
        val way = viewModel.view.activity.resources.getString(R.string.trace_spinner_way)
        if (viewModel.model().spinnerOption.equals(way))
            viewModel.model().chekedVisibility = View.VISIBLE
        else
            viewModel.model().chekedVisibility = View.GONE
        return super.initAdapter(binding)
    }

    private fun defineVM(model: ILatLngModel, resources: Resources): IEditVM {
        if (model.spinnerOption.equals(resources.getString(R.string.trace_spinner_way)))
            return viewModelWayPoints
        else if (model.spinnerOption.equals(resources.getString(R.string.trace_spinner_end)))
            return viewModelEnd
        else if (model.spinnerOption.equals(resources.getString(R.string.trace_spinner_start)))
            return viewModelStart
        else if (model.spinnerOption.equals(resources.getString(R.string.trace_spinner_load)))
            return viewModelLoad
        return viewModelLoad
    }

//    override fun onDetach() {
//        super.onDetach()
//        viewModelWayPoints.panelModel = panelModel
//        viewModelWayPoints.onDestroy()
//    }
}
