package ru.android.zheka.di.cpanel

import dagger.Binds
import dagger.Module
import ru.android.zheka.fragment.HideGeo
import ru.android.zheka.fragment.IHideGeo

@Module
abstract class HideGeoModule {
    @Binds
    abstract fun bindActivity(context: HideGeo?): IHideGeo?
}