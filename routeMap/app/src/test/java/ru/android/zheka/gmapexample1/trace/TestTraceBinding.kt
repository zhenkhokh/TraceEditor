package ru.android.zheka.gmapexample1.trace

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.Trace

@Module
abstract class TestTraceBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [TestTraceBindingModule::class])
    abstract fun TraceFragment(): Trace?
}