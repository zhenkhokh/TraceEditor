package ru.android.zheka.di.map

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.model.MapModel
import ru.android.zheka.vm.MapVM
import ru.android.zheka.vm.IMapVM

@Module(includes = [MapModule::class])
class MapBindingModule {
    @Provides
    fun bindMapVM(model: MapModel?, view: IActivity?): IMapVM {
        return MapVM(view, model)
    }

    @Provides
    fun provideMapModel(view: Context?): MapModel {
        return MapModel(view)
    }
}