package ru.android.zheka.di;

import dagger.Binds;
import dagger.Module;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

@Module
public abstract class HomeModule {
    @Binds
    @ActivityScope
    public abstract IPanelHomeVM bindHome(PanelHomeVM vm);

    @Binds
    @ActivityScope
    public abstract IActivity bindActivity(Home fragment);
}
