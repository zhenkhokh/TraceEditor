package ru.android.zheka.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.google.android.gms.maps.model.LatLng

class Trace : Model() {
    @Column(name = "name")
    var name: String? = null

    @Column(name = "start")
    var start: LatLng? = null

    @Column(name = "end")
    var end: LatLng? = null

    //use , for lat-lng delimiter and ; for points delimiter
    @Column(name = "data")
    var data: DataTrace? = null

    //public PolylineOptions data;
    override fun toString(): String {
        val util = UtilePointSerializer()
        val util1 = UtileTracePointsSerializer()
        return StringBuilder().append("name:")
                .append(name)
                .append(" start:")
                .append(util.serialize(start!!) as String)
                .append(" end:")
                .append(util.serialize(end!!) as String)
                .append("data:")
                .append(util1.serialize(data!!) as String).toString()
    }
}