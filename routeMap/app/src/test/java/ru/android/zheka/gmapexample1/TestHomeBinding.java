package ru.android.zheka.gmapexample1;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ru.android.zheka.di.FragmentScope;
import ru.android.zheka.fragment.Home;

@Module
public abstract class TestHomeBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = {//TestApplicationModule.class
    })
    abstract Home homeFragment();

//    @Binds
//    public abstract IActivity bindActivity(Home context);
}
