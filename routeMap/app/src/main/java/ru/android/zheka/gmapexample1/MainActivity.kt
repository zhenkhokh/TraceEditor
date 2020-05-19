package ru.android.zheka.gmapexample1

import android.app.Activity
import android.os.Bundle
import com.activeandroid.Model
import com.activeandroid.query.Delete
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import ru.android.zheka.coreUI.AbstractActivity
import ru.android.zheka.db.Config
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Trace
import ru.android.zheka.fragment.Home
import ru.android.zheka.fragment.Settings
import ru.android.zheka.gmapexample1.databinding.ActivityHomeBinding
import ru.android.zheka.model.LatLngModel
import javax.inject.Inject

class MainActivity : AbstractActivity<ActivityHomeBinding>(), HasAndroidInjector //RoboActivity
{
    @Inject
    lateinit var latLngModel: LatLngModel

    companion object {
        @kotlin.jvm.JvmField
        var googleKey = ""
        lateinit var _androidInjector: DispatchingAndroidInjector<Any>
    }

    @Inject
    lateinit var androidInjecto: DispatchingAndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> {
        _androidInjector = if (::androidInjecto.isInitialized ) androidInjecto
        else _androidInjector
        return _androidInjector
    }

    protected var resViewId = R.layout.activity_home
    override val layoutId
        get() = R.layout.activity_home

    override fun initComponent() {
        AndroidInjection.inject(this)
    }

    override fun onInitBinding(binding: ActivityHomeBinding?) {
        switchToFragment(R.id.latLngFragment, Settings())
        switchToFragment(R.id.homeFragment, Home())
    }

    override fun onResumeBinding(binding: ActivityHomeBinding?) {
    }
    override fun onDestroyBinding(binding: ActivityHomeBinding?) {}

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config
        if (Application.isFieldNull(config)) {
            Delete().from(Trace::class.java).where("name=?", DbFunctions.DEFAULT_CONFIG_NAME).execute<Model>()
            Application.initConfig()
        }

        if (googleKey == "") googleKey = resources.getString(R.string.google_api_key)
        println("---------- " + System.getProperty("java.class.path"))
        setContentView(resViewId)
    }

    override fun getActivity(): Activity {
        return this
    }
}