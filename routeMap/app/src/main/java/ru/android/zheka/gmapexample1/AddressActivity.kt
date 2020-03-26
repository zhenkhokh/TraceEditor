package ru.android.zheka.gmapexample1

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import roboguice.activity.RoboActivity
import roboguice.inject.InjectView
import ru.android.zheka.db.Config
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.geo.GeoCoder
import ru.android.zheka.geo.GeoParserImpl
import ru.android.zheka.geo.GeoParserImpl.YandexGeoCoderException
import ru.android.zheka.gmapexample1.MainActivity
import ru.android.zheka.jsbridge.JsCallable

class AddressActivity : RoboActivity(), JsCallable {
    var clGeo: Class<*>? = null
    var context: Context = this
    var url = "file:///android_asset/address.html"
    private var region: TextView? = null
    private var city: TextView? = null
    private var street: TextView? = null
    private var house: TextView? = null

    @InjectView(R.id.webViewAddress)
    var webView: WebView? = null

    class MyDialog : CoordinateDialog() {
        var activity: AddressActivity? = null
        var clGeo: Class<*>? = null
        override fun process() {
            try {
                val longitude: String = lonField!!.text.toString()
                val latitude: String = latField!!.text.toString()
                val point = LatLng(latitude.toDouble()
                        , longitude.toDouble())
                val position = PositionInterceptor(activity)
                try {
                    position.positioning()
                } catch (e: Exception) {
                    position.updatePosition()
                }
                position.centerPosition = point
                val intent = position.newIntent
                intent.setClass(activity, clGeo)
                intent.action = Intent.ACTION_VIEW
                startActivity(intent)
                activity!!.finish()
            } catch (e: Exception) {
                val alert = AlertDialog("Неверный формат чиел. Углы должны быть дробными")
                alert.show(activity!!.fragmentManager, "Ошибка")
                e.printStackTrace()
            }
        }
    }

    override fun nextView(`val`: String) {
        val intent = intent
        if (`val`.contentEquals(HOME)) {
            intent.setClass(context, MainActivity::class.java)
            intent.action = Intent.ACTION_VIEW
            startActivity(intent)
            finish()
        }
        if (`val`.contentEquals(COORDINATE_GO)) {
            val dialog = MyDialog()
            dialog.activity = this
            dialog.clGeo = clGeo
            dialog.show(fragmentManager, "Переход")
        }
        if (`val`.contentEquals(ADDRESS_GO)) {
            if (isAllFieldCorrect) {
                geoCoder = try {
                    GeoParserImpl(region!!.text.toString()
                            , city!!.text.toString()
                            , street!!.text.toString()
                            , house!!.text.toString()).parse()
                } catch (e: YandexGeoCoderException) {
                    Toast.makeText(this, "Данные не получены, проверьте интернет соединение", 30).show()
                    return
                }
                val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config
                val sb = StringBuilder()
                sb.append(region!!.text.toString()).append(aDelimiter)
                        .append(city!!.text.toString()).append(aDelimiter)
                        .append(street!!.text.toString()).append(aDelimiter)
                        .append(house!!.text.toString())
                config.address = sb.toString()
                try {
                    DbFunctions.add(config)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                }
                if (geoCoder == null) {
                    val alert = AlertDialog("Неверные данные, не возможно получить координаты")
                    alert.show(fragmentManager, "Ошибка")
                    return
                }
                intent.setClass(context, AddressGoActivity::class.java)
                intent.action = Intent.ACTION_VIEW
                startActivity(intent)
                finish()
            } else {
                val alert = AlertDialog("Имеются пустые поля, используйте \"-\" для них")
                alert.show(fragmentManager, "Ошибка")
            }
        }
        if (`val`.contentEquals(CLEAR)) {
            val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config
            config.address = aDelimiter + aDelimiter + aDelimiter
            try {
                DbFunctions.add(config)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            }
            /*region.setText ("");
            city.setText ("");
            street.setText ("");
            house.setText ("");
            */intent.setClass(context, AddressActivity::class.java)
            intent.action = Intent.ACTION_VIEW
            startActivity(intent)
            finish()
        }
    }

    private val isAllFieldCorrect: Boolean
        private get() {
            if (region!!.text.toString().isEmpty()) return false
            if (city!!.text.toString().isEmpty()) return false
            if (street!!.text.toString().isEmpty()) return false
            return if (house!!.text.toString().isEmpty()) false else true
        }

    override fun getVebWebView(): WebView {
        return webView!!
    }

    public override fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_adress)
        try {
            clGeo = Class.forName("ru.android.zheka.gmapexample1.GeoPositionActivity")
            println("----------  from MainActivity: find  ru.android.zheka.gmapexample1.GeoPositionActivity")
        } catch (e: ClassNotFoundException) {
            println("---------- from MainActivity " + e.message)
        }
        val m = MenuHandler()
        m.initJsBridge(this, url)
        region = findViewById<View>(R.id.text_region) as TextView
        city = findViewById<View>(R.id.text_city) as TextView
        street = findViewById<View>(R.id.text_street) as TextView
        house = findViewById<View>(R.id.text_house) as TextView
        val config = DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config::class.java) as Config
        if (config.tenMSTime != "0") Toast.makeText(this, "Определение местоположения станет помехой, лучше его отключить", 30).show()
        var address = config.address
        if (!address.isEmpty()) {
            var endpos = address.indexOf(aDelimiter)
            region!!.text = address.substring(0, endpos)
            address = address.substring(endpos + 1)
            endpos = address.indexOf(aDelimiter)
            city!!.text = address.substring(0, endpos)
            address = address.substring(endpos + 1)
            endpos = address.indexOf(aDelimiter)
            street!!.text = address.substring(0, endpos)
            house!!.text = address.substring(endpos + 1)
        }
    }

    companion object {
        const val HOME = "home"
        const val COORDINATE_GO = "coordinateGo"
        const val ADDRESS_GO = "addressGo"
        const val CLEAR = "clearAddress"
        const val aDelimiter = "#"
        @JvmField
        var geoCoder: GeoCoder? = null
    }
}