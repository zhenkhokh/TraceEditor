package ru.android.zheka.geo

import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject
import ru.android.zheka.route.XMLParser
import java.util.*

class GeoCoderImpl(feedUrl: String?) : XMLParser(feedUrl), GeoCoder {
    private val addresses: MutableList<String>
    override val adresses: Array<String>
            get() = addresses.toTypedArray()
    private val _points: MutableList<LatLng>
    override val points: Array<LatLng>
        get()  = _points.toTypedArray()

    override fun getKey(address: String): Int {
        for (i in adresses.indices) {
            if (adresses[i] == address) return i
        }
        return -1
    }

    init {
        val `is` = inputStream ?: throw JSONException("io exception")
        val scanner = Scanner(`is`)
        var sb = StringBuilder()
        while (scanner.hasNext()) sb.append(scanner.next()).append(" ")
        println(sb.toString())
        //JSONObject json = new JSONObject (new String(sb.toString ().getBytes(Charset.forName ("UTF-8"))));
        val json = JSONObject(sb.toString())
        val response = json.getJSONObject("response")
        val geoObjectCollection = response.getJSONObject("GeoObjectCollection")
        val featureMember = geoObjectCollection.getJSONArray("featureMember")
        val n = featureMember.length()
        var index = 0
        while (scanner.hasNext()) sb.append(scanner.next()).append(" ")
        /*XmlPullParser xml = Xml.newPullParser ();
        try {
            xml.setInput (new BufferedReader(new InputStreamReader (is)));
            xml.re

        } catch (XmlPullParserException e) {
            e.printStackTrace ();
        }
        */
        addresses = ArrayList<String>()
        _points = ArrayList<LatLng>()
        while (index < n) {
            if (!featureMember.isNull(index++)) {
                var point: LatLng
                try {
                    val element = featureMember.getJSONObject(index - 1)
                    val geoObject = element.getJSONObject("GeoObject")
                    val geocoderMetaData = geoObject.getJSONObject("metaDataProperty")
                            .getJSONObject("GeocoderMetaData")
                    val status = geocoderMetaData["precision"].toString()
                    val name = geoObject["name"] as String
                    sb = StringBuilder()
                    sb.append(geoObject["description"].toString())
                            .append(", ")
                            .append(name)
                            .append(". Точность: ")
                            .append(status)
                    val sPoint = geoObject.getJSONObject("Point")["pos"].toString()
                    val lonLat = sPoint.split(" ").toTypedArray()
                    point = LatLng(java.lang.Double.valueOf(lonLat[1]), java.lang.Double.valueOf(lonLat[0]))
                } catch (e: JSONException) {
                    e.printStackTrace()
                    continue
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    continue
                }
                addresses.add(sb.toString())
                _points.add(point)
            }
        }
    }
}