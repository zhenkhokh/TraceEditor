package ru.android.zheka.gmapexample1;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.di.FragmentScope;
import ru.android.zheka.fragment.Home;

@Module
public abstract class TestHomeBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = {
    })
    abstract Home homeFragment();

    @Binds
    public abstract IActivity bindActivity(Home context);
}
