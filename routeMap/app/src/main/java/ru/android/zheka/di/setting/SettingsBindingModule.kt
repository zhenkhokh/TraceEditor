package ru.android.zheka.di.setting

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.model.GeoModel
import ru.android.zheka.vm.GeoVM
import ru.android.zheka.vm.IGeoVM

@Module(includes = [GeoModule::class])
class GeoBindingModule {
    @Provides
    fun bindGeoVM(model: GeoModel?, view: IActivity?): IGeoVM {
        return GeoVM(view!!, model!!)
    }

    @Provides
    fun provideGeoModel(view: Context?): GeoModel {
        return GeoModel(view)
    }
}