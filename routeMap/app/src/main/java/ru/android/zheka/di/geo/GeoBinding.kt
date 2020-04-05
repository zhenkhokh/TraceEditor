package ru.android.zheka.di.geo

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.Geo

@Module
abstract class GeoBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [GeoBindingModule::class])
    abstract fun geoFragment(): Geo?
}