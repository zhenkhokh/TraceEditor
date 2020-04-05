package ru.android.zheka.di.map

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.ActivityScope
import ru.android.zheka.gmapexample1.MapsActivity

@Module
abstract class MapMainBinding {
    @ActivityScope
    @ContributesAndroidInjector(modules = [MapBindingModule::class])
    abstract fun mainActivity(): MapsActivity?
}