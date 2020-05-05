package ru.android.zheka.di.home

import dagger.Binds
import dagger.Module
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.fragment.Home

@Module
abstract class HomeModule {
//    @Binds
//    abstract fun bindHomeModel(model: IPanelModel?): IHomeModel?

    @Binds
    abstract fun bindActivity(context: Home?): IActivity?
}