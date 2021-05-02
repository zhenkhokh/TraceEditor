package ru.android.zheka.geo

import org.json.JSONException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

class GeoParserImpl(region: String?, city: String?, street: String?, house: String?) : GeoParser(region!!, city!!, street!!, house!!) {
    override fun parse(key: String): GeoCoder {
        val url = "https://geocode-maps.yandex.ru/1.x/?apikey=$key&geocode="
        val sb = StringBuilder()
        sb.append(url)
        try {
            URLEncoder.encode("привет", "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        try {
            if (!region.isEmpty()) sb.append(URLEncoder.encode(region, encode)).append("+")
            if (!city.isEmpty()) sb.append(URLEncoder.encode(city, encode)).append("+")
            if (!street.isEmpty()) sb.append(URLEncoder.encode(street, encode)).append("+")
            if (!house.isEmpty()) sb.append(URLEncoder.encode(house, encode))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        sb.append("&format=json")
        return try {
            GeoCoderImpl( /*URLEncoder.encode(sb.toString (),encode)*/sb.toString())
        } catch (e: JSONException) {
            throw YandexGeoCoderException()
        } catch (e: Exception) {
            throw e
        }
    }

    inner class YandexGeoCoderException : RuntimeException()
    companion object {
        var encode = "UTF-8"
    }
}