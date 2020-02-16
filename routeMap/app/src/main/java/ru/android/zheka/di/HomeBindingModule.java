package ru.android.zheka.di;

import dagger.Module;
import dagger.Provides;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.model.HomeModel;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

@Module(includes = HomeModule.class)
public class HomeBindingModule {

    @Provides
    public IPanelHomeVM bindHomeVM(HomeModel model, IActivity view) {
        return new PanelHomeVM (view, model);
    }

    @Provides
    public HomeModel provideHomeModel(IActivity view) {
        return new HomeModel (view);
    }
}
