package ru.android.zheka.di.latlng

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.LatLng

@Module
abstract class LatLngBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [LatLngBindingModule::class])
    abstract fun latLngFragment(): LatLng?
}