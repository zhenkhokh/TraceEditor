package ru.android.zheka.di.map

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.Map

@Module
abstract class MapBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [MapBindingModule::class])
    abstract fun geoFragment(): Map?
}