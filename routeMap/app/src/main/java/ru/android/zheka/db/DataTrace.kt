package ru.android.zheka.db

import android.graphics.Color
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import java.util.*

class DataTrace {
    var extraPoints: ArrayList<String>? = ArrayList()
    private var cursor: Iterator<List<LatLng>?>? = null
    override fun toString(): String {
        return "DataTrace{" +
                "extraPoints=" + extraPoints +
                ", cursor=" + cursor +
                ", allPoints=" + allPoints +
                ", segmentPoints=" + segmentPoints +
                ", restPoints=" + restPoints +
                ", segments=" + segments +
                '}'
    }

    var allPoints: PolylineOptions
        private set
    private var segmentPoints: LinkedList<LatLng>? = null
    private var restPoints: LinkedList<LatLng>? = null
    private var segments: ArrayList<List<LatLng>>? = null
    fun copy(initialization: Boolean): DataTrace? {
        val dataTrace = DataTrace()
        if (extraPoints == null) return null
        dataTrace.extraPoints = ArrayList(extraPoints)
        if (segments == null) if (!initSegments()) return null
        dataTrace.segments = ArrayList(segments)
        if (initialization) {
            val util = UtilePointSerializer()
            val it: Iterator<String> = extraPoints!!.iterator()
            for (segment in segments!!) {
                dataTrace.addPoints(segment)
                if (it.hasNext()) {
                    val point = util.deserialize(it.next()) as LatLng
                    dataTrace.addPoint(point)
                }
            }
        }
        return dataTrace
    }

    fun removeFirstSegment(): List<LatLng?>? {
        return if (segments == null) null else segments!!.removeAt(0)
    }

    fun addPoints(points: List<LatLng?>?) {
        addPoints(points!!.toTypedArray())
    }

    fun addPoints(points: Array<LatLng?>) {
        allPoints.add(*points)
    }

    fun addPoint(sPoint: String) {
        val point = UtilePointSerializer().deserialize(sPoint) as LatLng
        addPoint(point)
    }

    fun addPoint(point: LatLng?) {
        if (point != null) allPoints.add(point)
    }

    fun nextSegment(): List<LatLng>? {
        if (segments == null) throw NullPointerException()
        if (cursor == null) cursor = segments!!.iterator()
        if (cursor!!.hasNext()) {
            return cursor!!.next()
        }
        throw IndexOutOfBoundsException()
    }

    fun nextSegmentArray(): Array<LatLng> {
        return nextSegment()!!.toTypedArray()!!
    }

    operator fun hasNext(): Boolean {
        if (segments == null) return false
        if (cursor == null) cursor = segments!!.iterator()
        return cursor!!.hasNext()
    }

    fun resetSegmentCursor() {
        if (segments == null) throw NullPointerException()
        cursor = segments!!.iterator()
    }

    fun removeHead(removedWayPoint: LatLng?): Boolean {
        if (getHeadOrTail(removedWayPoint, true)) {
            allPoints = configPolyOptions()
            addPoints(segmentPoints)
            return true
        }
        return false
    }

    fun initSegments(): Boolean {
        val allPoints = allPoints.points
        val utile = UtilePointSerializer()
        segments = ArrayList()
        for (sPoint in extraPoints!!) {
            val point = utile.deserialize(sPoint) as LatLng
            if (!initSegment(point)) {
                this.allPoints = configPolyOptions()
                addPoints(allPoints)
                return false
            }
        }
        segments!!.add(this.allPoints.points)
        this.allPoints = configPolyOptions()
        addPoints(allPoints)
        return true
    }

    private fun initSegment(point: LatLng): Boolean {
        if (getHeadOrTail(point, false)) {
            //segmentPoints.add(point);//link segments
            segments!!.add(segmentPoints as List<LatLng>)
            allPoints = configPolyOptions()
            val last = (segmentPoints)?.last()
            val removed = (restPoints )!!.removeFirst()
            if (last != null) (restPoints )!!.addFirst(last) //link segments
            else if (removed != point) {
                (restPoints)!!.addFirst(removed)
            } else if (removed == point) {
                println("removed waypoints from segment:$point")
            }
            addPoints(restPoints)
            return true
        }
        if (allPoints.points.size == segmentPoints!!.size) {
            segments!!.add(segmentPoints as List<LatLng>)
            restPoints!!.add(point) // end point
            return true
        }
        return false
    }

    private fun getHeadOrTail(wayPoint: LatLng?, forTail: Boolean): Boolean {
        var pass = if (forTail) false else true
        segmentPoints = LinkedList()
        restPoints = LinkedList()
        for (point in allPoints.points) {
            if (pass != forTail && point == wayPoint) //BellmannFord.round (point)
                pass = if (pass) false else true
            if (pass) segmentPoints!!.add(point) else restPoints!!.add(point)
        }
        return pass == forTail
    }

    companion object {
        fun configPolyOptions(): PolylineOptions {
            val polyoptions = PolylineOptions()
            polyoptions.color(Color.BLUE)
            polyoptions.width(10f)
            return polyoptions
        }
    }

    init {
        allPoints = configPolyOptions()
    }
}