package ru.android.zheka.route

import com.google.android.gms.maps.model.LatLng

//by Haseem Saheed
class Segment
/**
 * Create an empty segment.
 */
{
    /**
     * Points in this segment. *
     */
    var start: LatLng? = null

    /**
     * Turn instruction to reach next segment. *
     */
    var instruction: String? = null

    /**
     * Length of segment. *
     */
    var length = 0

    /**
     * Distance covered. *
     */
    var distance = 0.0


    /**
     * Creates a segment which is a copy of this one.
     *
     * @return a Segment that is a copy of this one.
     */
    fun copy(): Segment {
        val copy = Segment()
        copy.start = start
        copy.instruction = instruction
        copy.length = length
        copy.distance = distance
        return copy
    }
}