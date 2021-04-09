package ru.android.zheka.sound

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.android.zheka.sound.response.SoundResponse

class SoundParser {
    val retrofit: Retrofit
    val params:Sound

    companion object {
        val RU_CODE = "ru-RU"
        val EN_CODE = "en-US"
    }

    init {
        retrofit = Retrofit.Builder()
                .baseUrl("https://speech.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        params = Sound()
        params.enableWordTimeOffsets = "true"
    }

    fun buildContent(soundContent:String):SoundParser {
        params.audioContent = soundContent
        return this
    }

    fun parse(auth:String, lan:String = RU_CODE): Call<SoundResponse> {
        val service = retrofit.create(SpeechService::class.java)
        return service.soundText(lan, params.audioContent,
            params.enableWordTimeOffsets,auth)
    }
}
