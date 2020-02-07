package ru.android.zheka.gmapexample1;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import ru.android.zheka.di.AppComponent;
import ru.android.zheka.di.AppModule;

@Singleton
@Component( modules = TestApplicationModule.class)
public interface TestAppComponent extends AndroidInjector<RobolectricMainApp> {
        @Component.Builder
        interface Builder {
        @BindsInstance
        TestAppComponent.Builder application(Application application);
        Builder appModule(AppModule appModule);
//        Builder homeModule(HomeModule appModule);

        TestAppComponent build();
    }
}
