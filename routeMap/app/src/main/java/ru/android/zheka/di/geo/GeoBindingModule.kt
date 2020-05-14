package ru.android.zheka.di.geo

import dagger.Module
import dagger.Provides
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.model.GeoModel
import ru.android.zheka.vm.GeoVM
import ru.android.zheka.vm.IGeoVM

@Module(includes = [GeoModule::class])
class GeoBindingModule {
    @Provides
    fun provideGeoVM(model: GeoModel?, view: IActivity?): IGeoVM {
        return GeoVM(view!!, model!!)
    }
}