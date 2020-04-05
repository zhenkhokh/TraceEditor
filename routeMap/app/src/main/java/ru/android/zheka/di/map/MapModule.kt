package ru.android.zheka.di.map

import dagger.Binds
import dagger.Module
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.di.ActivityScope
import ru.android.zheka.fragment.Map
import ru.android.zheka.model.MapModel
import ru.android.zheka.model.IMapModel

@Module
abstract class MapModule {
    @Binds
    @ActivityScope
    abstract fun bindMapModel(model: MapModel?): IMapModel?

    @Binds
    abstract fun bindActivity(context: Map?): IActivity?
}