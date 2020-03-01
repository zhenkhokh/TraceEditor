package ru.android.zheka.gmapexample1.home;

import android.content.Context;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.model.HomeModel;
import ru.android.zheka.vm.IPanelHomeVM;

@Module(includes = TestHomeModule.class)
public class TestHomeBindingModule {
    IPanelHomeVM mockVM = Mockito.mock (IPanelHomeVM.class);

    @Provides
    public IPanelHomeVM bindHomeVM(HomeModel model, IActivity view) {
        return mockVM;//new PanelHomeVM (Mockito.mock(IActivity.class), Mockito.mock(HomeModel.class));//mockVM;// Mockito.mock (IPanelHomeVM.class)
    }
    @Provides
    public HomeModel provideHomeModel(Context view) {
        return new HomeModel (view);
    }
}
