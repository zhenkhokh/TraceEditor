package ru.android.zheka.sound.response

import ru.android.zheka.sound.ErrorDiscription

class ResponseError {
    lateinit var error: ErrorDiscription
    fun isInitialized() = this::error.isInitialized
//    @SerializedName("error.code")
//    lateinit var code:String
//    @SerializedName("message.code")
//    lateinit var message:String
//    @SerializedName("message.status")
//    lateinit var status:String
}