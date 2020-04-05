package ru.android.zheka.di.home

import dagger.Binds
import dagger.Module
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.di.ActivityScope
import ru.android.zheka.fragment.Home
import ru.android.zheka.model.HomeModel
import ru.android.zheka.model.IHomeModel

@Module
abstract class HomeModule {
    @Binds
    @ActivityScope
    abstract fun bindHomeModel(model: HomeModel?): IHomeModel?

    @Binds
    abstract fun bindActivity(context: Home?): IActivity?
}