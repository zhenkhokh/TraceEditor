package ru.android.zheka.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.gmapexample1.Application
import ru.android.zheka.model.GeoModel
import ru.android.zheka.model.HomeModel
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.model.MapModel
import javax.inject.Singleton

@Module
class AppModule(app: Application) {
    private val context: Context

    @Singleton
    @Provides
    fun bindSomeManger(): SomeManager {
        return SomeManager()
    }

    @Singleton
    @Provides
    fun provideLLModel(view: Context): LatLngModel {
        return LatLngModel(view)
    }

    @Singleton
    @Provides
    fun provideHomeModel(view: Context): IPanelModel {
        return HomeModel(view)
    }

    @Singleton
    @Provides
    fun provideContext(): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideGeoModel(view: Context?): GeoModel {
        return GeoModel(view)
    }

    @Singleton
    @Provides
    fun provideMapModel(view: Context?): MapModel {
        return MapModel(view)
    }

    init {
        context = app.applicationContext
    }
}