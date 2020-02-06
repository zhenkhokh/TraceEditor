package ru.android.zheka.di;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.gmapexample1.Application;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class
        , HomeBinding.class
})
public interface AppComponent extends AndroidInjector <Application> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        AppComponent.Builder application(Application application);
//        AppComponent.Builder homeModule(HomeModule homeModule);
        AppComponent build();
    }
}

