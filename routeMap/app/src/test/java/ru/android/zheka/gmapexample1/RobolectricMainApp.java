package ru.android.zheka.gmapexample1;


import android.content.Context;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;

import androidx.multidex.MultiDex;
import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import ru.android.zheka.db.Config;
import ru.android.zheka.db.Point;
import ru.android.zheka.db.Trace;
import ru.android.zheka.db.UtilePointSerializer;
import ru.android.zheka.db.UtileTracePointsSerializer;
import ru.android.zheka.gmapexample1.home.TestHomeBindingModule;

//TODO remove
public class RobolectricMainApp extends //Application
        DaggerApplication {
    @Override
    public void onCreate() {
        super.onCreate ();

        Configuration dbConfiguration = new Configuration.Builder (this)
                .setDatabaseName ("Navi.db")
                .setDatabaseVersion (1)
                .addModelClasses (Config.class, Point.class, Trace.class)
                .addTypeSerializers (UtilePointSerializer.class, UtileTracePointsSerializer.class)
                .create ();
        ActiveAndroid.initialize (dbConfiguration);
        Application.initConfig ();
        if (Application.optimizationBellmanFlag.isEmpty ())
            Application.optimizationBellmanFlag = getString (R.string.optimizationdata3);
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
