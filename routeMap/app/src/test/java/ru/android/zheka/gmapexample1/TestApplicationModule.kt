package ru.android.zheka.gmapexample1

import android.content.Context
import dagger.Module
import dagger.Provides

@Module //(includes = TestHomeModule.class)
class TestApplicationModule //    @Provides
//    public Home provideHome(){
//        return view;
//    }
(private val app: RobolectricMainApp) {
    @Provides
    fun provideApp(): Context {
        return app.applicationContext
    } //TODO

}