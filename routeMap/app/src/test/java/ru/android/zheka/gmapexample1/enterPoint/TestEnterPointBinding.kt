package ru.android.zheka.gmapexample1.enterPoint

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.FragmentScope
import ru.android.zheka.fragment.EnterPoint

@Module
abstract class TestEnterPointBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = [TestEnterPointBindingModule::class])
    abstract fun EnterPointFragment(): EnterPoint?
}