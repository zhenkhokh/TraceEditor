package ru.android.zheka.di.latlng

import dagger.Binds
import dagger.Module
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.di.ActivityScope
import ru.android.zheka.fragment.LatLng
import ru.android.zheka.model.ILatLngModel
import ru.android.zheka.model.LatLngModel

@Module
abstract class LatLngModule {
    @Binds
    @ActivityScope
    abstract fun bindLatLngModel(model: LatLngModel?): ILatLngModel?

    @Binds
    abstract fun bindActivity(context: LatLng?): IActivity?
}