package ru.android.zheka.di.cpanel

import dagger.Module
import dagger.Provides
import ru.android.zheka.fragment.IHideMap
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.HideMapPanelVM
import ru.android.zheka.vm.ILatLngVM

@Module(includes = [HideMapModule::class])
class HideMapBindingModule {
    @Provides
    fun provideMapModel(model: LatLngModel?, view: IHideMap?): ILatLngVM {
        return HideMapPanelVM(view!!, model!!)
    }
}