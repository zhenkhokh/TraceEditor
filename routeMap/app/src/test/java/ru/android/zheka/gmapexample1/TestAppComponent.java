package ru.android.zheka.gmapexample1;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import ru.android.zheka.fragment.Home;
import ru.android.zheka.vm.IPanelHomeVM;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class,
        TestHomeBinding.class,
        TestApplicationModule.class
//        AppModule.class
})
public interface TestAppComponent extends //AppComponent
        AndroidInjector <RobolectricMainApp>
{
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
    Home homeFragment();
}
