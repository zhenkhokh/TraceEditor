package ru.android.zheka.di.cpanel

import dagger.Binds
import dagger.Module
import ru.android.zheka.fragment.HideMap
import ru.android.zheka.fragment.IHideMap

@Module
abstract class HideMapModule {
    @Binds
    abstract fun bindActivity(context: HideMap?): IHideMap?
}