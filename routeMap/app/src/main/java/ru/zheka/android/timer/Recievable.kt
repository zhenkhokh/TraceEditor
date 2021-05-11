package ru.zheka.android.timer

import android.content.Context
import android.content.Intent

interface Recievable {
    fun onReceive(arg0: Context, intent: Intent)
}