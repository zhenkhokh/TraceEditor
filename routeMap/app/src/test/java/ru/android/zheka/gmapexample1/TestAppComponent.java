package ru.android.zheka.gmapexample1;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import ru.android.zheka.gmapexample1.edit.TestEditBinding;
import ru.android.zheka.gmapexample1.edit.TestEditBindingModule;
import ru.android.zheka.gmapexample1.editTraces.TestEditTracesBinding;
import ru.android.zheka.gmapexample1.enterPoint.TestEnterPointBinding;
import ru.android.zheka.gmapexample1.geo.TestGeoBinding;
import ru.android.zheka.gmapexample1.geo.TestGeoBindingModule;
import ru.android.zheka.gmapexample1.geo.TestGeoBinding_GeoFragment;
import ru.android.zheka.gmapexample1.home.TestHomeBinding;
import ru.android.zheka.gmapexample1.home.TestHomeBindingModule;
import ru.android.zheka.gmapexample1.home.TestHomeBinding_HomeFragment;
import ru.android.zheka.gmapexample1.trace.TestTraceBinding;
import ru.android.zheka.gmapexample1.trace.TestTraceBinding_TraceFragment;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class,
        TestHomeBinding.class,
        TestHomeBindingModule.class,
        TestGeoBinding.class,
        TestGeoBindingModule.class,
        TestEditBinding.class,
        TestEditBindingModule.class,
        TestEditTracesBinding.class,
//        TestEditTracesBindingModule.class,
        TestEnterPointBinding.class,
//        TestEnterPointBindingModule.class,
        TestTraceBinding.class,
        TestApplicationModule.class,
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
//        TestAppComponent.Builder testEditBindingModule(TestEditBindingModule module);
//        TestAppComponent.Builder testEnterPointBindingModule(TestEnterPointBindingModule module);
//        Builder homeModule(HomeModule appModule);

        TestAppComponent build();
    }
    void inject(ExampleUnitTest app);
    TestHomeBinding_HomeFragment.HomeSubcomponent.Factory homeSubcomponent();
    TestGeoBinding_GeoFragment.GeoSubcomponent.Factory geoSubcomponent();
    TestTraceBinding_TraceFragment.TraceSubcomponent.Factory traceSubcomponent();

//    IPanelHomeVM homeVM();
//    Home homeFragment();
}
