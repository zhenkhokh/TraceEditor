package ru.android.zheka.gmapexample1.home;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.android.zheka.di.FragmentScope;
import ru.android.zheka.fragment.Home;

@Module
public abstract class TestHomeBinding {
        @FragmentScope
        @ContributesAndroidInjector(modules = { TestHomeBindingModule.class
        })
        abstract Home homeFragment();
}
