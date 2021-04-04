package ru.android.zheka.sound

class ErrorDiscription {
    lateinit var code:String
    lateinit var message:String
    lateinit var status:String

    override fun toString(): String {
        return "Code: "+code+"; message:" + message +"; status:" + status
    }
}
