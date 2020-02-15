package ru.android.zheka.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.android.zheka.fragment.Home;

@Module
public abstract class HomeBinding {
        @FragmentScope
        @ContributesAndroidInjector(modules = { HomeBindingModule.class
        })
        abstract Home homeFragment();
}
