package ru.android.zheka.gmapexample1.editTraces

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentChildScope
import ru.android.zheka.fragment.EditTraces

@Module
abstract class TestEditTracesBinding {
    @FragmentChildScope
    @ContributesAndroidInjector(modules = [ TestEditTracesBindingModule::class])
    abstract fun editTracesFragment(): EditTraces?
}