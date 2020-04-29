package ru.android.zheka.di.trace

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentChildScope
import ru.android.zheka.di.edit.EditBindingModule
import ru.android.zheka.fragment.Trace

@Module
abstract class TraceBinding {
    @FragmentChildScope
    @ContributesAndroidInjector(modules = [EditBindingModule::class, TraceBindingModule::class])
    abstract fun traceFragment(): Trace?
}