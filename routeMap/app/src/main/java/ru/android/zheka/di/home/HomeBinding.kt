package ru.android.zheka.di.home

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.PanelScope
import ru.android.zheka.fragment.Home

@Module
abstract class HomeBinding {
    @PanelScope
    @ContributesAndroidInjector(modules = [HomeBindingModule::class])
    abstract fun homeFragment(): Home?
}