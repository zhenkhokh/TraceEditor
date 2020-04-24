package ru.android.zheka.di.edit

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.Edit

@Module
abstract class EditBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [EditBindingModule::class])
    abstract fun EditFragment(): Edit?
}