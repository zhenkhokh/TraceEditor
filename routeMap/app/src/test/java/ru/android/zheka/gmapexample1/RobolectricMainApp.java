package ru.android.zheka.gmapexample1;


import android.content.Context;

import androidx.multidex.MultiDex;
import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import ru.android.zheka.di.AppComponent;
import ru.android.zheka.gmapexample1.home.TestHomeBindingModule;

//TODO remove
public class RobolectricMainApp extends //Application
        DaggerApplication
{
    AppComponent component;

    public AppComponent getAppComponent() {
        return component;
    }

//    @Inject
//    DispatchingAndroidInjector <Fragment> fragmentInjector;
//
//    @Override
//    public AndroidInjector androidInjector() {
//        return fragmentInjector;
//    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext (base);
        MultiDex.install (this);
    }

    @Override
    protected AndroidInjector <? extends DaggerApplication> applicationInjector() {
        return DaggerTestAppComponent.builder ()
                .application (this)
                .testApplicationModule (new TestApplicationModule (this))
                .testHomeBindingModule (new TestHomeBindingModule ())
                .build ();
    }
}
