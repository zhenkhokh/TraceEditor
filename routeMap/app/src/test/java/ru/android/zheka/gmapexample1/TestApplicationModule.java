package ru.android.zheka.gmapexample1;

import org.mockito.Mockito;

import dagger.Module;
import dagger.Provides;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.model.IHomeModel;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

@Module(includes = TestModule.class)
public class TestApplicationModule  {
    private Home view = Mockito.mock(Home.class);
    private IHomeModel model = Mockito.mock(IHomeModel.class);

    @Provides
    public IPanelHomeVM bindHome(IActivity view) {
        return new PanelHomeVM(view, model);
    }

//    @Binds
//    @ActivityScope//TODO
//    public abstract IActivity bindActivity(Home fragment);

    @Provides
    public IHomeModel bindHomeModel() {
        return model;
    }

    @Provides
    public Home provideHome(){
        return new Home ();
    }
}
