package ru.android.zheka.gmapexample1.home;

import dagger.Binds;
import dagger.Module;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.model.HomeModel;
import ru.android.zheka.model.IHomeModel;

@Module
public abstract class TestHomeModule {
    @Binds
//    @ActivityScope
    public abstract IHomeModel bindHomeModel(HomeModel model);

    @Binds
    public abstract IActivity bindActivity(Home context);

//    @Binds
//    public abstract IPanelHomeVM bindHomeVM(PanelHomeVM vm);
}
