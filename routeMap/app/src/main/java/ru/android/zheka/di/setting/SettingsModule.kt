package ru.android.zheka.di.setting

import dagger.Binds
import dagger.Module
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.di.ActivityScope
import ru.android.zheka.fragment.Geo
import ru.android.zheka.model.GeoModel
import ru.android.zheka.model.IGeoModel

@Module
abstract class GeoModule {
    @Binds
    @ActivityScope
    abstract fun bindGeoModel(model: GeoModel?): IGeoModel?

    @Binds
    abstract fun bindActivity(context: Geo?): IActivity?
}