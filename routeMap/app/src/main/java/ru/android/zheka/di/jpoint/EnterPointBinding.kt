package ru.android.zheka.di.jpoint

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.EnterPoint

@Module
abstract class EnterPointBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [EnterPointBindingModule::class])
    abstract fun enterPointFragment(): EnterPoint?
}