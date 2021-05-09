package ru.android.zheka.db

import com.activeandroid.Model
import com.activeandroid.annotation.Column

class Config : Model() {
    @Column(name = "name")
    var name: String? = null

    @Column(name = "optimization")
    var optimization: Boolean? = null

    @Column(name = "travelmode")
    var travelMode: String? = null

    @JvmField
    @Column(name = "updatelocation")
    var uLocation: Boolean? = null

    @Column(name = "updateTime")
    var tenMSTime: String? = null

    @Column(name = "avoid")
    var avoid: String? = null

    @Column(name = "reserved1") // do not modify name
    var bellmanFord //can be renamed
            : String? = null

    @Column(name = "reserved2")
    var address: String? = null

    @Column(name = "reserved3")
    var rateLimit_ms: String? = null

    @Column(name = "reserved4")
    var offline: String? = null

    //@Column(name = "reserved5")
    //public String reserved5;
    override fun toString(): String {
        val sb = StringBuilder()
        return sb.append("name: $name")
                .append(" optimization:$optimization")
                .append(" travelMode:$travelMode")
                .append(" show coordinate changing:$uLocation")
                .append(" update location time:$tenMSTime")
                .append(" avoid:$avoid")
                .append(" bellmanFord:$bellmanFord")
                .append(" address:$address")
                .append(" rateLimit_ms:$rateLimit_ms")
                .append(" offline:$offline")
                .toString()
    }
}