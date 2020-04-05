package ru.android.zheka.gmapexample1

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.databinding.ViewDataBinding
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
import ru.android.zheka.jsbridge.JavaScriptMenuHandler
import ru.android.zheka.jsbridge.JsCallable
import javax.inject.Inject

class MainActivity : AbstractActivity<ViewDataBinding?>(), HasAndroidInjector //RoboActivity
{
    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector!!
    }

    protected var url = "file:///android_asset/home.html"
    protected var resViewId = R.layout.activity_home
    var clGeo: Class<*>? = null
    var clLatLng: Class<*>? = null
    var clPtoTr: Class<*>? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_home
    }

    override fun initComponent() {}

    override fun onInitBinding(binding: ViewDataBinding?) {}
    override fun onResumeBinding(binding: ViewDataBinding?) {}
    override fun onDestroyBinding(binding: ViewDataBinding?) {}

    public override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this) //TODO
        super.onCreate(savedInstanceState)
        val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config
        if (Application.Companion.isFieldNull(config)) {
            Delete().from(Trace::class.java).where("name=?", DbFunctions.DEFAULT_CONFIG_NAME).execute<Model>()
            Application.Companion.initConfig()
        }
        switchToFragment(R.id.homeFragment, Home())
        if (googleKey == "") googleKey = resources.getString(R.string.google_api_key)
        println("---------- " + System.getProperty("java.class.path"))
        try {
            clGeo = Class.forName("ru.android.zheka.gmapexample1.GeoPositionActivity")
            println("----------  from MainActivity: find  ru.android.zheka.gmapexample1.GeoPositionActivity")
        } catch (e: ClassNotFoundException) {
            println("---------- from MainActivity " + e.message)
        }
        try {
            clLatLng = Class.forName("ru.android.zheka.gmapexample1.LatLngActivity")
            println("----------  from MainActivity: find  ru.android.zheka.gmapexample1.LatLngActivity")
        } catch (e: ClassNotFoundException) {
            println("---------- from MainActivity " + e.message)
        }
        try {
            clPtoTr = Class.forName("ru.android.zheka.gmapexample1.PointToTraceActivity")
            println("----------  from MainActivity: find  ru.android.zheka.gmapexample1.PointToTraceActivity")
        } catch (e: ClassNotFoundException) {
            println("---------- from MainActivity " + e.message)
        }
        setContentView(resViewId)
    }

    override fun getActivity(): Activity {
        return this
    }
    companion object {
        @kotlin.jvm.JvmField
        var googleKey = ""
        const val SETTINGS = "settings"
        const val EDIT_TRACE = "editTrace"
        const val EDIT_POINT = "editPoint"
        const val GEO = "geo"
        const val TO_TRACE = "toTrace"
        const val POINTS = "points"
        const val GO = "address"
        const val INFO = "info"

    }
}

internal class MenuHandler {
    fun initJsBridge(menu: JsCallable, url: String?) {
        println("start initJsBridge from " + menu.javaClass.name)
        //menu.getActivity().setContentView(resViewId);
        println("start setWebViewSettings")
        val webViewHome = menu.vebWebView
        println("start setJavaScriptEnabled, webHome is $webViewHome")
        webViewHome.settings.javaScriptEnabled = true
        println("start setWebViewSettings")
        setWebViewSettings(webViewHome, menu as Activity)
        println("start addJavascriptInterface")
        webViewHome.addJavascriptInterface(JavaScriptMenuHandler(menu), "menuHandler")
        println("start loadUrl")
        webViewHome.loadUrl(url)
        println("end initJsBridge")
    }

    fun setWebViewSettings(webViewHome: WebView, context: Context?) {
        println("start set webClient")
        webViewHome.webChromeClient = object : WebChromeClient() {
            override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
                AlertDialog.Builder(context)
                        .setTitle("javaScript dialog")
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok
                        ) { dialog, which ->
                            println("hello from positive")
                            result.confirm()
                        }
                        .setCancelable(false)
                        .create()
                        .show()
                return true
            }
        }
        println("end set webClient")
    }
}