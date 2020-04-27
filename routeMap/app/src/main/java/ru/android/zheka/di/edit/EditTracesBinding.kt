package ru.android.zheka.di.edit

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentChildScope
import ru.android.zheka.fragment.EditTraces

@Module
abstract class EditTracesBinding {
    @FragmentChildScope
    @ContributesAndroidInjector(modules = [EditBindingModule::class, EditTracesBindingModule::class])
    abstract fun editTracesFragment(): EditTraces?
}