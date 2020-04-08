package ru.android.zheka.gmapexample1.geo

import dagger.Binds
import dagger.Module
import ru.android.zheka.fragment.Geo
import ru.android.zheka.fragment.IGeo
import ru.android.zheka.model.GeoModel
import ru.android.zheka.model.IGeoModel
import ru.android.zheka.vm.GeoVM
import ru.android.zheka.vm.IGeoVM

@Module
abstract class TestGeoModule {
    @Binds //    @ActivityScope
    abstract fun bindGeoModel(model: GeoModel?): IGeoModel?

    @Binds
    abstract fun bindActivity(context: Geo?): IGeo?

    @Binds //    @ActivityScope
    abstract fun bindGeoVM(vm: GeoVM?): IGeoVM?
}