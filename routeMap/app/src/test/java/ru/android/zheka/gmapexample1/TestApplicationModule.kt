package ru.android.zheka.gmapexample1

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.coreUI.PanelModel
import ru.android.zheka.model.*

@Module
class TestApplicationModule
(private val app: RobolectricMainApp) {
    @Provides
    fun provideApp(): Context {
        return app.applicationContext
    }
    @Provides
    fun provideGeoModel(view: Context?): GeoModel {
        return GeoModel(view)
    }
    @Provides
    fun provideHomeModel(view: Context?): HomeModel {
        return HomeModel(view)
    }

    @Provides
    fun provideLatLngModel(view: Context?): LatLngModel {
        return LatLngModel(view)
    }

    @Provides
    fun provideSettingsModel(view: Context?): ISettingsModel {
        return SettingsModel(view!!)
    }

    @Provides
    fun providePanelModel(view: Context?): IPanelModel {
        return PanelModel(view!!)
    }
}