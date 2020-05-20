package ru.android.zheka.gmapexample1

import android.content.Context
import androidx.multidex.MultiDex
import com.activeandroid.ActiveAndroid
import com.activeandroid.Configuration
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import ru.android.zheka.db.*
import ru.android.zheka.gmapexample1.Application.Companion.initConfig
import ru.android.zheka.gmapexample1.geo.TestGeoBindingModule
import ru.android.zheka.gmapexample1.home.TestHomeBindingModule

class RobolectricMainApp : DaggerApplication() {
    override fun onCreate() {
        super.onCreate()
        val dbConfiguration = Configuration.Builder(this)
                .setDatabaseName("Navi.db")
                .setDatabaseVersion(1)
                .addModelClasses(Config::class.java, Point::class.java, Trace::class.java)
                .addTypeSerializers(UtilePointSerializer::class.java, UtileTracePointsSerializer::class.java)
                .create()
        ActiveAndroid.initialize(dbConfiguration)
        initConfig()
//        if (Application.optimizationBellmanFlag.isEmpty()) Application.optimizationBellmanFlag = getString(R.string.optimizationdata3)
    }

    //    @Inject
    //    DispatchingAndroidInjector <Fragment> fragmentInjector;
    //
    //    @Override
    //    public AndroidInjector androidInjector() {
    //        return fragmentInjector;
    //    }

    override fun attachBaseContext(base: Context) {
        try {
            super.attachBaseContext(base)
            MultiDex.install(this)
        } catch (ignored: RuntimeException) {
            // Multidex support doesn't play well with Robolectric yet
        }
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerTestAppComponent.builder()
                .application(this)
                .testApplicationModule(TestApplicationModule(this))
//                .testHomeBindingModule(TestHomeBindingModule())
//                .testGeoBindingModule(TestGeoBindingModule())
                .build()
    }
}