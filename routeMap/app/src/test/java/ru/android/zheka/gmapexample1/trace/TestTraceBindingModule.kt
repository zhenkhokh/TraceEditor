package ru.android.zheka.gmapexample1.trace

import dagger.Module
import dagger.Provides
import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.di.FragmentChildScope
import ru.android.zheka.fragment.Edit
import ru.android.zheka.fragment.ITrace
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.trace.TraceEndVM
import ru.android.zheka.vm.trace.TraceLoadVM
import ru.android.zheka.vm.trace.TraceStartVM
import ru.android.zheka.vm.trace.TraceWayPointsVM

@Module(includes = [])
class TestTraceBindingModule {
    @Provides
    fun provideTraceLoadVM(model: LatLngModel?, view: ITrace?): TraceLoadVM {
        return TraceLoadVM(view!!, model!!)
    }

    @Provides
    fun provideTraceStartVM(model: LatLngModel?, view: ITrace?): TraceStartVM {
        return TraceStartVM(view!!, model!!)
    }

    @Provides
    fun provideTraceEndVM(model: LatLngModel?, view: ITrace?): TraceEndVM {
        return TraceEndVM(view!!, model!!)
    }

    @Provides
    fun provideTraceWayVM(model: LatLngModel?, view: ITrace?, panelModel: IPanelModel): TraceWayPointsVM {
        return TraceWayPointsVM(view!!, model!!, panelModel)
    }

    @FragmentChildScope
    @Provides
    fun provideEdit(): Edit {
        return Edit()
    }
}