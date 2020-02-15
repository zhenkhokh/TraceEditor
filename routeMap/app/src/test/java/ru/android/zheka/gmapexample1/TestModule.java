package ru.android.zheka.gmapexample1;

import dagger.Binds;
import dagger.Module;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.fragment.Home;

@Module
public abstract class TestModule {
    @Binds
    abstract public IActivity bindHome(Home home);
}
