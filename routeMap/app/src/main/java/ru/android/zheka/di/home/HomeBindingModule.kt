package ru.android.zheka.di.home

import dagger.Module
import dagger.Provides
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.di.PanelScope
import ru.android.zheka.fragment.*
import ru.android.zheka.vm.IPanelHomeVM
import ru.android.zheka.vm.PanelHomeVM

@Module(includes = [HomeModule::class])
class HomeBindingModule {
    @PanelScope
    @Provides
    fun bindHomeVM(model: IPanelModel?, view: IActivity?, edit: IEdit, editTraces: IEditTraces,
        trace: ITrace, enterPoint: IEnterPoint): IPanelHomeVM {
        return PanelHomeVM(view!!, model!!, edit, editTraces, trace, enterPoint)
    }

    @PanelScope
    @Provides
    fun provideEdit(): IEdit {
        return Edit()
    }

    @PanelScope
    @Provides
    fun provideEditTraces(): IEditTraces {
        return EditTraces()
    }

    @PanelScope
    @Provides
    fun provideTrace(): ITrace {
        return Trace()
    }

    @PanelScope
    @Provides
    fun provideEnterPoint(): IEnterPoint {
        return EnterPoint()
    }

//    @Provides
//    fun provideHomeModel(view: Context?): HomeModel {
//        return HomeModel(view)
//    }
}