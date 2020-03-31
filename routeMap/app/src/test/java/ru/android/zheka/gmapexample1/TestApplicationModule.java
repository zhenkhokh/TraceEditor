package ru.android.zheka.gmapexample1;

import android.content.Context;

import dagger.Module;
import dagger.Provides;

@Module//(includes = TestHomeModule.class)
public class TestApplicationModule {
    private RobolectricMainApp app;

    public TestApplicationModule(RobolectricMainApp app) {
        this.app = app;
    }

    @Provides
    public Context provideApp() {
        return app.getApplicationContext ();
    }
//TODO
//    @Provides
//    public Home provideHome(){
//        return view;
//    }
}
