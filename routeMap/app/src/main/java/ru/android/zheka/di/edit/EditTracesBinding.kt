package ru.android.zheka.di.edit

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.EditTraces

@Module//(subcomponents = [EditBinding_EditFragment.EditSubcomponent::class])
abstract class EditTracesBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [EditBindingModule::class, EditTracesBindingModule::class])
    abstract fun editTracesFragment(): EditTraces?
}