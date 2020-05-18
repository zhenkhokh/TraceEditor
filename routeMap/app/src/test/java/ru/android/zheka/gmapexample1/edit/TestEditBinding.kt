package ru.android.zheka.gmapexample1.edit

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.Edit

@Module
abstract class TestEditBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [TestEditBindingModule::class])
    abstract fun EditFragment(): Edit?
}