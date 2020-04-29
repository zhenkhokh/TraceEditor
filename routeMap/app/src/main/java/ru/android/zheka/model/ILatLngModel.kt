package ru.android.zheka.model

import androidx.databinding.ObservableField

interface ILatLngModel {
    fun titleText(): ObservableField<String>
    val checked: HashSet<String>
    var spinnerOption:String
    val trigered: Boolean
}