package ru.android.zheka.di.cpoint

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentChildScope
import ru.android.zheka.di.edit.EditBindingModule
import ru.android.zheka.fragment.CPoint

@Module
abstract class CPointBinding {
    @FragmentChildScope
    @ContributesAndroidInjector(modules = [EditBindingModule::class, CPointBindingModule::class])
    abstract fun editTracesFragment(): CPoint?
}