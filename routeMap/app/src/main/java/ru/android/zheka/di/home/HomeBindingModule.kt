package ru.android.zheka.di.home

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.model.HomeModel
import ru.android.zheka.vm.IPanelHomeVM
import ru.android.zheka.vm.PanelHomeVM

@Module(includes = [HomeModule::class])
class HomeBindingModule {
    @Provides
    fun bindHomeVM(model: HomeModel?, view: IActivity?): IPanelHomeVM {
        return PanelHomeVM(view, model)
    }

    @Provides
    fun provideHomeModel(view: Context?): HomeModel {
        return HomeModel(view)
    }
}