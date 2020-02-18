package ru.android.zheka.gmapexample1;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import ru.android.zheka.gmapexample1.di.TestHomeBinding;
import ru.android.zheka.gmapexample1.di.TestHomeBindingModule;
import ru.android.zheka.gmapexample1.di.TestHomeBinding_HomeFragment;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class,
        TestHomeBinding.class,
        TestHomeBindingModule.class,
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
        TestAppComponent.Builder testHomeBindingModule(TestHomeBindingModule module);
//        Builder homeModule(HomeModule appModule);

        TestAppComponent build();
    }
    void inject(ExampleUnitTest app);
    TestHomeBinding_HomeFragment.HomeSubcomponent.Factory homeSubcomponent();
//    IPanelHomeVM homeVM();
//    Home homeFragment();
}
