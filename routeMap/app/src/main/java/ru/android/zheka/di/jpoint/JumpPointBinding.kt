package ru.android.zheka.di.jpoint

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentChildScope
import ru.android.zheka.di.edit.EditBindingModule
import ru.android.zheka.fragment.JumpPoint

@Module
abstract class JumpPointBinding {
    @FragmentChildScope
    @ContributesAndroidInjector(modules = [EditBindingModule::class, JumpPointBindingModule::class])
    abstract fun jumpPointFragment(): JumpPoint?
}