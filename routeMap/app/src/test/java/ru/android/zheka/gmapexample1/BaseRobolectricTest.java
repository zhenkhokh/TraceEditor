package ru.android.zheka.gmapexample1;

import org.junit.Before;
import org.robolectric.annotation.Config;

@Config(manifest = "AndroidManifest.xml", sdk = 28,application = RobolectricMainApp.class)
public class BaseRobolectricTest {

    private RobolectricMainApp application;

    @Before
    public void setUp() {
//        application = androidx.test.core.app.ApplicationProvider.getApplicationContext ();
//        application.provider.init(this);
    }
}