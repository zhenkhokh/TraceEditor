package ru.android.zheka.gmapexample1

import android.content.Context
import dagger.Module
import dagger.Provides

@Module //(includes = TestHomeModule.class)
class TestApplicationModule (private val app: RobolectricMainApp)//    @Provides
//    public Home provideHome(){
//        return view;
//    }
 {
    @Provides
    fun provideApp(): Context? {
        return app.applicationContext
    } //TODO

}