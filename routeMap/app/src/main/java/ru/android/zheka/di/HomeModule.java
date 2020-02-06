package ru.android.zheka.di;

import dagger.Binds;
import dagger.Module;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.model.HomeModel;
import ru.android.zheka.model.IHomeModel;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

@Module
public abstract class HomeModule {
    @Binds
    @ActivityScope
    public abstract IPanelHomeVM bindHome(PanelHomeVM vm);

    @Binds
    @ActivityScope//TODO
    public abstract IActivity bindActivity(Home fragment);

    @Binds
    @ActivityScope//TODO
    public abstract IHomeModel bindHomeModel(HomeModel view);
}
