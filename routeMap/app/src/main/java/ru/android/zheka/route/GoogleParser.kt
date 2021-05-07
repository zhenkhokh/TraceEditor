package ru.android.zheka.route

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

class GoogleParser(feedUrl: String?) : XMLParser(feedUrl), Parser {
    /**
     * Distance covered. *
     */
    private var distance = 0

    /**
     * Parses a url pointing to a Google JSON object to a Route object.
     *
     * @return a Route object based on the JSON object by Haseem Saheed
     */
    override fun parse(): Route? {
        // turn the stream into a string
        result = convertStreamToString(this.inputStream)
        if (result == null) return null

        //Create an empty route
        val route = Route()
        //Create an empty segment
        val segment = Segment()
        try {
            /*if (runException++%9==0)
                throw new JSONException("my Exception");
                */
            //Tranform the string into a json object
            val json = JSONObject(result)
            //Get the route object
            val jsonRoute = json.getJSONArray("routes").getJSONObject(0)
            //Get the leg, only one leg as we don't support waypoints
            val leg = jsonRoute.getJSONArray("legs").getJSONObject(0)
            //Get the steps for this leg
            val steps = leg.getJSONArray("steps")
            //Number of steps for use in for loop
            val numSteps = steps.length()
            //Set the name of this route using the start & end addresses
            route.name = leg.getString("start_address") + " to " + leg.getString("end_address")
            //Get google's copyright notice (tos requirement)
            route.copyright = jsonRoute.getString("copyrights")
            //Get the total length of the route.
            route.length = leg.getJSONObject("distance").getInt("value")
            //Get any warnings provided (tos requirement)
            if (!jsonRoute.getJSONArray("warnings").isNull(0)) {
                route.warning = jsonRoute.getJSONArray("warnings").getString(0)
            }
            var s = true
            var index = 0
            while (s) if (!jsonRoute.getJSONArray("waypoint_order").isNull(index)) route.order.add(jsonRoute.getJSONArray("waypoint_order").getInt(index++)) else s = false
            /* Loop through the steps, creating a segment for each one and
                     * decoding any polylines found as we go to add to the route object's
                     * map array. Using an explicit for loop because it is faster!
                     */for (i in 0 until numSteps) {
                //Get the individual step
                val step = steps.getJSONObject(i)
                //Get the start position for this step and set it on the segment
                val start = step.getJSONObject("start_location")
                segment.start = LatLng(start.getDouble("lat"),
                        start.getDouble("lng"))
                //Set the length of this segment in metres
                val length = step.getJSONObject("distance").getInt("value")
                distance += length
                segment.length = length
                segment.distance = distance / 1000.toDouble()
                //Strip html from google directions and set as turn instruction
                segment.instruction = step.getString("html_instructions").replace("<(.*?)*>".toRegex(), "")
                //Retrieve & decode this segment's polyline and add it to the route.
                route.addPoints(decodePolyLine(step.getJSONObject("polyline").getString("points")))
                //Push a copy of the segment to the route
                route.addSegment(segment.copy())
            }
        } catch (e: JSONException) {
            Log.e("Routing Error", e.message!!)
            e.printStackTrace()
            println("result=$result")
            return null
        }
        return route
    }

    /**
     * Decode a polyline string into a list of GeoPoints.
     *
     * @param poly polyline encoded string to decode.
     * @return the list of GeoPoints represented by this polystring.
     */
    private fun decodePolyLine(poly: String): List<LatLng> {
        val len = poly.length
        var index = 0
        val decoded: MutableList<LatLng> = ArrayList()
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = poly[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = poly[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            decoded.add(LatLng(
                    lat / 100000.0, lng / 100000.0
            ))
        }
        return decoded
    }

    companion object {
        var result: String? = null
        var runException = 0

        /**
         * Convert an inputstream to a string.
         *
         * @param input inputstream to convert.
         * @return a String of the inputstream.
         */
        private fun convertStreamToString(input: InputStream?): String? {
            if (input == null) return null
            val reader = BufferedReader(InputStreamReader(input))
            val sBuf = StringBuilder()
            var line: String? = null
            try {
                while (reader.readLine().also { line = it } != null) {
                    sBuf.append(line)
                }
            } catch (e: IOException) {
                Log.e("Routing Error", e.message!!)
            } finally {
                try {
                    input.close()
                } catch (e: IOException) {
                    Log.e("Routing Error", e.message!!)
                }
            }
            return sBuf.toString()
        }
    }
}