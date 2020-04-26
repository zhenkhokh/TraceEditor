package ru.android.zheka.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.android.zheka.gmapexample1.Application
import javax.inject.Singleton

@Module
class AppModule(app: Application) {
    private val context: Context

    @Singleton
    @Provides
    fun bindSomeManger(): SomeManager {
        return SomeManager()
    }

    @Singleton
    @Provides
    fun provideContext(): Context {
        return context
    }

    init {
        context = app.applicationContext
    }
}