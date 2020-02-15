package ru.android.zheka.di;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.model.HomeModel;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

@Module
public abstract class HomeBindingModule {
    @Binds
    public abstract IActivity bindActivity(Home context);

    @Provides
    public static IPanelHomeVM bindHome(HomeModel model, IActivity view) {
        return new PanelHomeVM (view, model);
    }
}
