package ru.android.zheka.gmapexample1.geo

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.Geo

@Module
abstract class TestGeoBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [TestGeoBindingModule::class])
    abstract fun geoFragment(): Geo?
}