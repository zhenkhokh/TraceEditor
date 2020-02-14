package ru.android.zheka.gmapexample1;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;
import ru.android.zheka.di.HomeBinding;
import ru.android.zheka.vm.IPanelHomeVM;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class,
        HomeBinding.class,
        TestApplicationModule.class,
//        AppModule.class
})
public interface TestAppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        TestAppComponent.Builder application(RobolectricMainApp application);

        TestAppComponent.Builder testApplicationModule(TestApplicationModule appModule);
//        Builder homeModule(HomeModule appModule);

        TestAppComponent build();
    }
    void inject(ExampleUnitTest app);
    IPanelHomeVM homeVM();
}
