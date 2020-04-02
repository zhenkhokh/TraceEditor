package ru.android.zheka.gmapexample1

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class TestApplicationModule
(private val app: RobolectricMainApp) {
    @Provides
    fun provideApp(): Context {
        return app.applicationContext
    }
}