package ru.android.zheka.gmapexample1;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.di.AppModule;
import ru.android.zheka.di.HomeBinding;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;
import org.mockito.Mockito;

@Module
public class TestApplicationModule extends AppModule {
    private final PanelHomeVM panelHomeVM;

    public TestApplicationModule(PanelHomeVM panelHomeVM) {
        this.panelHomeVM = panelHomeVM;
    }
    @Provides
    @Singleton
    public PanelHomeVM provideVM(){
        return Mockito.mock(PanelHomeVM.class);//panelHomeVM;
    }

    @Provides
    @Singleton
    public IPanelHomeVM provideIVM(){
        return Mockito.mock(IPanelHomeVM.class);//panelHomeVM
    }

    @Provides
    @Singleton
    public IActivity provideHome(){
        return Mockito.mock(Home.class);//panelHomeVM;
    }
}
