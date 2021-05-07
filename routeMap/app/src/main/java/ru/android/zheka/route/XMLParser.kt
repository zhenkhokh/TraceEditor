package ru.android.zheka.route

import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.net.MalformedURLException
import java.net.URL

//by Haseem Saheed
open class XMLParser protected constructor(feedUrl: String?) {
    protected var feedUrl: URL? = null
    protected val inputStream: InputStream?
        protected get() = try {
            feedUrl!!.openConnection().getInputStream()
        } catch (e: IOException) {
            Log.e("XMLParser Error", e.message!!)
            null
        }

    companion object {
        // names of the XML tags
        protected const val MARKERS = "markers"
        protected const val MARKER = "marker"
    }

    init {
        try {
            this.feedUrl = URL(feedUrl)
        } catch (e: MalformedURLException) {
            Log.e("Routing Error", e.message!!)
        }
    }
}