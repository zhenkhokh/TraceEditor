package ru.android.zheka.sound

import com.google.gson.annotations.SerializedName

class Sound {

    @SerializedName("config.languageCode")
    lateinit var languageCode:String

    @SerializedName("audio.content")
    lateinit var audioContent:String

    @SerializedName("config.enableWordTimeOffsets")
    lateinit var enableWordTimeOffsets:String
}
