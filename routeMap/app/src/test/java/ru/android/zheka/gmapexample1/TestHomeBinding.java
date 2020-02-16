package ru.android.zheka.gmapexample1;

import org.mockito.Mockito;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.di.FragmentScope;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.model.IHomeModel;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

@Module(includes = TestApplicationModule.class)
public abstract class TestHomeBinding {
    @FragmentScope
    @ContributesAndroidInjector(modules = {TestApplicationModule.class
    })
    abstract Home homeFragment();

    @Binds
    public abstract IActivity bindActivity(Home context);

    static private Home view = new Home ();//Mockito.mock(Home.class);
    static private IHomeModel model = Mockito.mock(IHomeModel.class);

    @Provides
    public static IPanelHomeVM bindHome(IActivity view) {
        return new PanelHomeVM (view, model);
    }

    @Provides
    public static IHomeModel bindHomeModel() {
        return model;
    }
}
