package ru.android.zheka.sound

import retrofit2.Call
import retrofit2.http.*
import ru.android.zheka.sound.response.SoundResponse

interface SpeechService {
    //TODO use RxJava instead Call --> com.squareup.retrofit2:adapter-rxjava2:
//    @Headers("Content-Type: application/x-www-form-urlencoded")
    @FormUrlEncoded
    @POST("v1/speech:recognize")
    fun soundText_(@Field("config.languageCode") languageCode: String,
                   @Field("audio.content") aContent: String,
                   @Field("config.enableWordTimeOffsets") enableWordTimeOffsets:String,
                   @Query("key") auth:String): Call<SoundResponse>


    @POST("v1/speech:recognize")
    fun soundText(@Body params: Sound, @Query("key") auth:String): Call<SoundResponse>
}