package ru.android.zheka.di.geo

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.ActivityScope
import ru.android.zheka.gmapexample1.GeoPositionActivity

@Module
abstract class GeoMainBinding {
    @ActivityScope
    @ContributesAndroidInjector(modules = [GeoBindingModule::class])
    abstract fun mainActivity(): GeoPositionActivity?
}