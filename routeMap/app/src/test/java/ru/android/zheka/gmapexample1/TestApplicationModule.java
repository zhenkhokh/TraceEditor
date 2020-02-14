package ru.android.zheka.gmapexample1;

import org.mockito.Mockito;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.android.zheka.model.IHomeModel;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

@Module
public class TestApplicationModule  {
    //    private IActivity view = Mockito.mock(IActivity.class);

    private IHomeModel model = Mockito.mock(IHomeModel.class);

    @Provides
    @Singleton
    public IPanelHomeVM bindHome() {
        return new PanelHomeVM(model);
    }

//    @Binds
//    @ActivityScope//TODO
//    public abstract IActivity bindActivity(Home fragment);

    @Provides
    @Singleton
    public IHomeModel bindHomeModel() {
        return model;
    }
}
