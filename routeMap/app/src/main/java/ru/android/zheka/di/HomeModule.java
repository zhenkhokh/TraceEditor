package ru.android.zheka.di;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.gmapexample1.MainActivity;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

@Module
public  class HomeModule {
    @Provides
    @ActivityScope
    public IPanelHomeVM bindHome() {
        return new PanelHomeVM (bindActivity ());
    }

    @Provides
    @ActivityScope
    public IActivity bindActivity() {return new Home ();}
}
