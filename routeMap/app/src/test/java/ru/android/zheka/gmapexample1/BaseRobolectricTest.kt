package ru.android.zheka.gmapexample1

import com.google.android.gms.maps.model.LatLng
import org.junit.Before
import org.robolectric.annotation.Config
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Point
import ru.android.zheka.db.Trace
import ru.android.zheka.db.UtilePointSerializer

@Config(manifest = "AndroidManifest.xml", sdk = [28], application = RobolectricMainApp::class)
open class BaseRobolectricTest {
    private val application: RobolectricMainApp? = null
    protected var point: Point? = null
    protected var trace: Trace? = null

    @Before
    fun setUp() {
//        application = androidx.test.core.app.ApplicationProvider.getApplicationContext ();
//        application.provider.init(this);

//        Configuration dbConfiguration = new Configuration.Builder (RobolectricMockTestRule.getApplication ())
//                .setDatabaseName ("Navi.db")
//                .addModelClasses (ru.android.zheka.db.Config.class, Point.class, Trace.class)
//                .addTypeSerializers (UtilePointSerializer.class, UtileTracePointsSerializer.class)
//                .create ();
//        ActiveAndroid.initialize (dbConfiguration);
//        Application.initConfig ();
        point = Point()
        trace = Trace()
        val names = arrayOf("1", "2")
        for (i in names.indices) {
            val namePoint = names[i]
            //dataTmp.add(map);
            val p = Point()
            p.name = namePoint
            p.data = UtilePointSerializer().deserialize("55.9896291,37.2334412") as LatLng
            //p.save();
            println("add point in latlng")
            if (!DbFunctions.exsistPoint(namePoint)) {
                println("not exsistPoint:$namePoint")
                try {
                    DbFunctions.add(p)
                } catch (e: IllegalAccessException) {
                    e.printStackTrace()
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                } catch (e: InstantiationException) {
                    e.printStackTrace()
                }
                println("adding $namePoint")
            }
        }
    }
}