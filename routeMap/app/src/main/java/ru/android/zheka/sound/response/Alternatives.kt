package ru.android.zheka.sound.response

class Alternatives {
    lateinit var alternatives: ArrayList<Alternative>
    fun isInitialized() = this::alternatives.isInitialized
}