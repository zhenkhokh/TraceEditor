package ru.android.zheka.gmapexample1

import android.content.Context
import androidx.multidex.MultiDex
import com.activeandroid.ActiveAndroid
import com.activeandroid.Configuration
import dagger.android.AndroidInjector
import dagger.android.support.DaggerApplication
import ru.android.zheka.db.*
import ru.android.zheka.di.AppComponent
import ru.android.zheka.di.AppModule
import ru.android.zheka.di.DaggerAppComponent
import ru.android.zheka.route.Routing.TravelMode
import java.util.*

//import android.support.multidex.MultiDexApplication;
//TODO use it for activeandroid
class Application : DaggerApplication() //extends MultiDexApplication
{
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
        if (optimizationBellmanFlag.isEmpty()) optimizationBellmanFlag = getString(R.string.optimizationdata3)
    }

    // extends SomeOtherApp
    override fun attachBaseContext(base: Context) {
        try {
            super.attachBaseContext(base)
            MultiDex.install(this)
        } catch (ignored: RuntimeException) {
            // Multidex support doesn't play well with Robolectric yet
        }
    }

    override fun applicationInjector(): AppComponent? {
        return DaggerAppComponent.builder()!!
                .application(this)!!
                .appModule(AppModule(this))!!
                .build()
    }

    companion object {
        @kotlin.jvm.JvmField
        var optimizationBellmanFlag = ""
        @kotlin.jvm.JvmStatic
        fun initConfig() {
            println("initConfig")
            var config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config?
            println("config is $config")
            if (isFieldNull(config) || config!!.rateLimit_ms.isEmpty()) {
                config = Config()
                config.name = DbFunctions.DEFAULT_CONFIG_NAME
                config.optimization = false
                config.travelMode = TravelMode.WALKING.toString()
                config.uLocation = true // prefer move camera to location rather than intent
                config.tenMSTime = "0"
                config.avoid = "tolls" // tolls, highways, ferries, indoor or empty
                config.bellmanFord = ""
                //config.reserved2 = "";
                config.address = AddressActivity.aDelimiter + AddressActivity.aDelimiter + AddressActivity.aDelimiter
                config.rateLimit_ms = "800"
                config.offline = DbFunctions.DEFAULT_CONFIG_OFFLINE
                val w: TravelMode = TravelMode.valueOf(config.travelMode)
                println("init $config")
                println("Routing.TravelModel.WALKING is clear $w")
                try {
                    DbFunctions.add(config)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                } catch (e: ConcurrentModificationException) {
                    DbFunctions.delete(config)
                    initConfig()
                }
            } else println("Config was succesfully get $config")
        }

        fun isFieldNull(config: Config?): Boolean {
            if (config == null) return true
            val fields = config.javaClass.fields
            for (i in fields.indices) {
                try {
                    if (fields[i][config] == null) return true
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                }
            }
            return false
        }
    }
}