package ru.android.zheka.di.home

import dagger.Module
import dagger.android.ContributesAndroidInjector
import ru.android.zheka.di.ActivityScope
import ru.android.zheka.gmapexample1.MainActivity

@Module
abstract class MainBinding {
    @ActivityScope
    @ContributesAndroidInjector(modules = [HomeBindingModule::class])
    abstract fun mainActivity(): MainActivity? //    @Provides
    //    public static MainActivity provideActivity() {
    //        return new MainActivity ();
    //    }
}