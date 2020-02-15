package ru.android.zheka.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.android.zheka.gmapexample1.MainActivity;

@Module
public abstract class MainBinding {
    @ActivityScope
    @ContributesAndroidInjector(modules = {HomeBindingModule.class
    })
    abstract MainActivity mainActivity();

//    @Provides
//    public static MainActivity provideActivity() {
//        return new MainActivity ();
//    }
}
