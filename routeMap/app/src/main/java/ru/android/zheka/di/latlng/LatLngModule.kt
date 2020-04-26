package ru.android.zheka.di.latlng

import dagger.Binds
import dagger.Module
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.fragment.LatLng

@Module
abstract class LatLngModule {
//    @Binds
//    abstract fun bindLatLngModel(model: LatLngModel?): ILatLngModel?

    @Binds
    abstract fun bindActivity(context: LatLng?): IActivity?
}