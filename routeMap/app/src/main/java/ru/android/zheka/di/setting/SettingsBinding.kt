package ru.android.zheka.di.setting

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.Settings

@Module
abstract class SettingsBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [SettingsBindingModule::class])
    abstract fun settingsFragment(): Settings?
}