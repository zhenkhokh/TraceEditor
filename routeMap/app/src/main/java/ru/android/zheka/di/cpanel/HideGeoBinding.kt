package ru.android.zheka.di.cpanel

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.HideGeo

@Module
abstract class HideGeoBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [HideGeoBindingModule::class])
    abstract fun hideGeoFragment(): HideGeo?
}