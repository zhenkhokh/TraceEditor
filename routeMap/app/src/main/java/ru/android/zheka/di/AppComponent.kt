package ru.android.zheka.di

import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import ru.android.zheka.di.edit.EditBinding
import ru.android.zheka.di.edit.EditMainBinding
import ru.android.zheka.di.geo.GeoBinding
import ru.android.zheka.di.geo.GeoMainBinding
import ru.android.zheka.di.home.HomeBinding
import ru.android.zheka.di.home.MainBinding
import ru.android.zheka.di.latlng.LatLngBinding
import ru.android.zheka.di.map.MapBinding
import ru.android.zheka.di.map.MapMainBinding
import ru.android.zheka.di.setting.SettingsBinding
import ru.android.zheka.gmapexample1.Application
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, AppModule::class
    , HomeBinding::class
    , MainBinding::class
    , GeoBinding::class
    , GeoMainBinding::class
    , MapBinding::class
    , MapMainBinding::class
    , SettingsBinding::class
    , LatLngBinding::class
    , EditMainBinding::class
    , EditBinding::class])
interface AppComponent : AndroidInjector<Application?> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application?): Builder?
        fun appModule(appModule: AppModule?): Builder?

        //        Builder homeModule(HomeModule appModule);
        fun build(): AppComponent?
    }
}