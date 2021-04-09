package ru.android.zheka.sound

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Query
import ru.android.zheka.sound.response.SoundResponse

interface SpeechService {
    //TODO use RxJava instead Call --> com.squareup.retrofit2:adapter-rxjava2:
    @FormUrlEncoded
    @POST("v1/speech:recognize")
    fun soundText(@Field("config.languageCode") languageCode: String,
                  @Field("audio.content") aContent: String,
                  @Field("config.enableWordTimeOffsets") enableWordTimeOffsets:String,
                  @Query("key") auth:String): Call<SoundResponse>
}