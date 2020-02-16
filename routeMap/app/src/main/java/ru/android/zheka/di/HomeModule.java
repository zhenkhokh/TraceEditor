package ru.android.zheka.di;

import dagger.Binds;
import dagger.Module;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.model.HomeModel;
import ru.android.zheka.model.IHomeModel;

@Module
public abstract class HomeModule {
    @Binds
    @ActivityScope
    public abstract IHomeModel bindHomeModel(HomeModel model);

    @Binds
    public abstract IActivity bindActivity(Home context);
}
