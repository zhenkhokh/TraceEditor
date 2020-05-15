package ru.android.zheka.di.cpanel

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.HideMap

@Module
abstract class HideMapBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [HideMapBindingModule::class])
    abstract fun hideMapFragment(): HideMap?
}