package ru.android.zheka.di.home

import dagger.Module
import dagger.Provides
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.di.PanelScope
import ru.android.zheka.fragment.*
import ru.android.zheka.model.IHomeModel
import ru.android.zheka.vm.IPanelHomeVM
import ru.android.zheka.vm.PanelHomeVM

@Module(includes = [HomeModule::class])
class HomeBindingModule {
    @PanelScope
    @Provides
    fun bindHomeVM(model: IHomeModel?, view: IActivity?, edit: IEdit, editTraces: IEditTraces,
        trace: ITrace): IPanelHomeVM {
        return PanelHomeVM(view!!, model!!, edit!!, editTraces, trace)
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

//    @Provides
//    fun provideHomeModel(view: Context?): HomeModel {
//        return HomeModel(view)
//    }
}