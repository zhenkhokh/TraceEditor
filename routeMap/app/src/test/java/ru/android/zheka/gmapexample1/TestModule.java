package ru.android.zheka.gmapexample1;

import org.mockito.Mockito;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.model.IHomeModel;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

@Module
public abstract class TestModule {
    @Binds
    abstract public IActivity bindHome(Home home);

    static private Home view = Mockito.mock(Home.class);
    static private IHomeModel model = Mockito.mock(IHomeModel.class);

    @Provides
    static public IPanelHomeVM bindHome(IActivity view) {
        return new PanelHomeVM (view, model);
    }

    @Provides
    static public IHomeModel bindHomeModel() {
        return model;
    }

//    @Provides
//    public Home provideHome(){
//        return new Home ();
//    }
}
