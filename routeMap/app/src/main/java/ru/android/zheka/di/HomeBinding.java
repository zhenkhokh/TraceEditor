package ru.android.zheka.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.android.zheka.fragment.Home;

@Module
abstract public class HomeBinding {
    @ActivityScope
    @ContributesAndroidInjector(modules = {HomeModule.class
    })
    abstract Home homeFragment();
}

