package ru.android.zheka.gmapexample1;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.robolectric.annotation.Config;

import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.db.Point;
import ru.android.zheka.db.Trace;
import ru.android.zheka.db.UtilePointSerializer;

@Config(manifest = "AndroidManifest.xml", sdk = 28,application = RobolectricMainApp.class)
public class BaseRobolectricTest {

    private RobolectricMainApp application;
    protected Point point;
    protected Trace trace;

    @Before
    public void setUp() {
//        application = androidx.test.core.app.ApplicationProvider.getApplicationContext ();
//        application.provider.init(this);

//        Configuration dbConfiguration = new Configuration.Builder (RobolectricMockTestRule.getApplication ())
//                .setDatabaseName ("Navi.db")
//                .addModelClasses (ru.android.zheka.db.Config.class, Point.class, Trace.class)
//                .addTypeSerializers (UtilePointSerializer.class, UtileTracePointsSerializer.class)
//                .create ();
//        ActiveAndroid.initialize (dbConfiguration);
//        Application.initConfig ();
        point = new Point ();
        trace = new Trace ();
        String[] names = {"1", "2"};
        for (int i = 0; i < names.length; i++) {
            String namePoint = names[i];
            //dataTmp.add(map);
            Point p = new Point ();
            p.name = namePoint;
            p.data = (LatLng) new UtilePointSerializer ().deserialize ("55.9896291,37.2334412");
            //p.save();
            System.out.println ("add point in latlng");
            if (!DbFunctions.exsistPoint (namePoint)) {
                try {
                    DbFunctions.add (p);
                } catch (IllegalAccessException e) {
                    e.printStackTrace ();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace ();
                } catch (InstantiationException e) {
                    e.printStackTrace ();
                }
                System.out.println ("adding " + namePoint);
            }
        }
    }
}