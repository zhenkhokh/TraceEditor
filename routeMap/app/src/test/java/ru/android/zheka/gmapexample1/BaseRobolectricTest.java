package ru.android.zheka.gmapexample1;

import org.junit.Before;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import ru.android.zheka.di.DaggerAppComponent;

@Config(manifest = "AndroidManifest.xml", sdk = 28,application = RobolectricMainApp.class)
public class BaseRobolectricTest {

    private RobolectricMainApp application;

    @Before
    public void setUp() {
//        application = androidx.test.core.app.ApplicationProvider.getApplicationContext ();
//        application.provider.init(this);
    }

    public RobolectricAppComponent getAppComponent() {
        return null;//((RobolectricAppComponent)application.component)
                //.testModule(new TestModule ())
    //            .build();//(RobolectricAppComponent) application.applicationInjector ();
    }
}