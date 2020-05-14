package ru.android.zheka.di.cpanel

import dagger.Module
import dagger.Provides
import ru.android.zheka.fragment.IHideGeo
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.HideGeoPanelVM
import ru.android.zheka.vm.ILatLngVM

@Module(includes = [HideGeoModule::class])
class HideGeoBindingModule {
    @Provides
    fun provideGeoModel(model: LatLngModel?, view: IHideGeo?): ILatLngVM {
        return HideGeoPanelVM(view!!, model!!)
    }
}