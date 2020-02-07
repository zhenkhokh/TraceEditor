package ru.android.zheka.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module//(includes = {HomeModule.class})
public class AppModule {
    @Singleton
    @Provides
    public SomeManger bindSomeManger() {
        return new SomeManger();
    }
}
