package ru.android.zheka.di

import android.content.Context
import dagger.Module
import dagger.Provides
import ru.android.zheka.gmapexample1.Application
import ru.android.zheka.model.LatLngModel
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
    fun provideLLModel(view: Context): LatLngModel {
        return LatLngModel(view)
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