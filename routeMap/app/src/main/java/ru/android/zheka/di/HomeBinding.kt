package ru.android.zheka.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.fragment.Home

@Module
abstract class HomeBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [HomeBindingModule::class])
    abstract fun homeFragment(): Home?
}