package ru.android.zheka.di;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.model.HomeModel;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

@Module
public class HomeBindingModule {
    @Provides
    @ActivityScope
    public IActivity bindActivity(Context context) {
        return new Home ();
    }

    @Provides
    @ActivityScope
    public IPanelHomeVM bindHome(HomeModel model) {
        return new PanelHomeVM (model);
    }
}
