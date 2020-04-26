package ru.android.zheka.di.latlng

import dagger.Module
import dagger.Provides
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.ILatLngVM
import ru.android.zheka.vm.LatLngVM

@Module(includes = [LatLngModule::class])
class LatLngBindingModule {
    @Provides
    fun bindLatLngVM(model: LatLngModel?, view: IActivity?): ILatLngVM {
        return LatLngVM(view!!, model!!)
    }

//    @Provides
//    fun provideLatLngModel(view: Context?): LatLngModel {
//        return LatLngModel(view!!)
//    }
}