package ru.android.zheka.di.edit

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.ActivityScope
import ru.android.zheka.gmapexample1.EditActivity

@Module
abstract class EditMainBinding {
    @ActivityScope
    @ContributesAndroidInjector
    abstract fun mainActivity(): EditActivity?
}