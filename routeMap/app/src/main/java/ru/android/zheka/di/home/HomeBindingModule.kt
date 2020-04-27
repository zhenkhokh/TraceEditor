package ru.android.zheka.di.home

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.di.PanelScope
import ru.android.zheka.fragment.Edit
import ru.android.zheka.fragment.EditTraces
import ru.android.zheka.fragment.IEdit
import ru.android.zheka.fragment.IEditTraces
import ru.android.zheka.model.HomeModel
import ru.android.zheka.vm.IPanelHomeVM
import ru.android.zheka.vm.PanelHomeVM

@Module(includes = [HomeModule::class])
class HomeBindingModule {
    @PanelScope
    @Provides
    fun bindHomeVM(model: HomeModel?, view: IActivity?, edit: IEdit, editTraces: IEditTraces): IPanelHomeVM {
        return PanelHomeVM(view!!, model!!, edit!!, editTraces)
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

    @Provides
    fun provideHomeModel(view: Context?): HomeModel {
        return HomeModel(view)
    }
}