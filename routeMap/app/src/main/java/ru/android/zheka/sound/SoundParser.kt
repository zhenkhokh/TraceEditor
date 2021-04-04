package ru.android.zheka.sound

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.android.zheka.sound.response.SoundResponse

class SoundParser(val soundContent:String) {
    val retrofit: Retrofit
    val params:Sound

    init {
//        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

//        val client:OkHttpClient = OkHttpClient.Builder()
//                //.addInterceptor(interceptor)
//                .build()
        retrofit = Retrofit.Builder()
                .baseUrl("https://speech.googleapis.com")
//                .client(client)
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        params = Sound()
        params.audioContent = soundContent
        params.languageCode = "ru-RU"
        params.enableWordTimeOffsets = "true"
    }

    fun parse(auth:String): Call<SoundResponse> {
        val service = retrofit.create(SpeechService::class.java)
        return service.soundText(params, auth)
    }

    fun parse_(auth:String): Call<SoundResponse> {
        val service = retrofit.create(SpeechService::class.java)
        return service.soundText_(params.languageCode, params.audioContent,
            params.enableWordTimeOffsets,auth)
    }
}