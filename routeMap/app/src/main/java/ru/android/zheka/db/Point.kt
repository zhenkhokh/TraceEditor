package ru.android.zheka.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column
import com.google.android.gms.maps.model.LatLng

class Point : Model() {
    @Column(name = "name")
    var name: String = ""

    @Column(name = "data")
    var data: LatLng? = null
    override fun toString(): String {
        val util = UtilePointSerializer()
        return StringBuilder().append("name:")
                .append(name)
                .append(" geo:")
                .append(util.serialize(data!!) as String).toString()
    }
}