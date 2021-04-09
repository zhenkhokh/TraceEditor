package ru.android.zheka.sound.response

import java.util.*

class SoundResponse {
    lateinit var results: ArrayList<Alternatives>
    fun isInitialized() = this::results.isInitialized
}
