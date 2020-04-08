package ru.android.zheka.gmapexample1;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import ru.android.zheka.gmapexample1.geo.TestGeoBinding;
import ru.android.zheka.gmapexample1.geo.TestGeoBindingModule;
import ru.android.zheka.gmapexample1.geo.TestGeoBinding_GeoFragment;
import ru.android.zheka.gmapexample1.home.TestHomeBinding;
import ru.android.zheka.gmapexample1.home.TestHomeBindingModule;
import ru.android.zheka.gmapexample1.home.TestHomeBinding_HomeFragment;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class,
        TestHomeBinding.class,
        TestHomeBindingModule.class,
        TestGeoBinding.class,
        TestGeoBindingModule.class,
        TestApplicationModule.class
//        AppModule.class
})
public interface TestAppComponent extends AndroidInjector <RobolectricMainApp> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        TestAppComponent.Builder application(RobolectricMainApp application);

        TestAppComponent.Builder testApplicationModule(TestApplicationModule appModule);
        TestAppComponent.Builder testHomeBindingModule(TestHomeBindingModule module);
        TestAppComponent.Builder testGeoBindingModule(TestGeoBindingModule module);

//        Builder homeModule(HomeModule appModule);

        TestAppComponent build();
    }
    void inject(ExampleUnitTest app);
    TestHomeBinding_HomeFragment.HomeSubcomponent.Factory homeSubcomponent();
    TestGeoBinding_GeoFragment.GeoSubcomponent.Factory geoSubcomponent();

//    IPanelHomeVM homeVM();
//    Home homeFragment();
}
