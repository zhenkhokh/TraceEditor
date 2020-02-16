package ru.android.zheka.di;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.android.zheka.gmapexample1.Application;

@Module
public class AppModule {
    private final Context context;

    public AppModule(Application app) {
        this.context = app.getApplicationContext ();
    }

    @Singleton
    @Provides
    public SomeManger bindSomeManger() {
        return new SomeManger ();
    }

    @Singleton
    @Provides
    public Context provideContext() {return context;}
}
