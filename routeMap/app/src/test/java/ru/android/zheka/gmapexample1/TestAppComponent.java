package ru.android.zheka.gmapexample1;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import ru.android.zheka.di.ActivityScope;
import ru.android.zheka.di.AppComponent;
import ru.android.zheka.di.HomeBinding;
import ru.android.zheka.di.HomeModule;
import ru.android.zheka.fragment.Home;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class,
        TestApplicationModule.class,
})
public interface TestAppComponent extends AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        TestAppComponent.Builder application(RobolectricMainApp application);

        Builder appModule(TestApplicationModule appModule);
//        Builder homeModule(HomeModule appModule);

        TestAppComponent build();
    }
    void inject(ExampleUnitTest app);

}
