package ru.android.zheka.gmapexample1;


import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;
import ru.android.zheka.di.AppComponent;

//TODO remove
public class RobolectricMainApp extends Application //implements HasAndroidInjector
{
    AppComponent component;

    //    @Override
//    public void onCreate() {
//        super.onCreate();
//        component = DaggerRobolecticAppComponent.builder()
//                .testModule(new TestModule ()).build();
//        component.inject(this);
//}
    public AppComponent getAppComponent() {
        return component;
    }

    public DaggerMockProvider provider = new DaggerMockProvider ();

    //@Inject
//    DispatchingAndroidInjector<Fragment> fragmentInjector;
//
//    @Override
//    public AndroidInjector  androidInjector() {
//        return fragmentInjector;
//    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext (base);
        MultiDex.install (this);
    }

}
