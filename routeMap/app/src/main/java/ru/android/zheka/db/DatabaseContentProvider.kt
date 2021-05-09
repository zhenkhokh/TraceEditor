package ru.android.zheka.db

import com.activeandroid.Configuration
import com.activeandroid.content.ContentProvider

class DatabaseContentProvider : ContentProvider() {
    override fun getConfiguration(): Configuration {
        val builder = Configuration.Builder(context)
        builder.addModelClass(Config::class.java)
        builder.addModelClass(Trace::class.java)
        builder.addModelClass(Point::class.java)
        return builder.create()
    }
}