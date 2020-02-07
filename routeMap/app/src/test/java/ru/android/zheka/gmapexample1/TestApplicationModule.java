package ru.android.zheka.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.android.zheka.di.AppModule;
import ru.android.zheka.vm.PanelHomeVM;

@Module
public class TestApplicationModule extends AppModule {
    private final PanelHomeVM panelHomeVM;

    public TestApplicationModule(PanelHomeVM panelHomeVM) {
        this.panelHomeVM = panelHomeVM;
    }
    @Provides
    @Singleton
    public PanelHomeVM bindHome(){
        return panelHomeVM;
    }
}
