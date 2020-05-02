package ru.android.zheka.model

import androidx.databinding.ObservableField

interface ILatLngModel {
    fun titleText(): ObservableField<String>
    var chekedVisibility: Int
    var checked: ArrayList<Boolean>
    var spinnerOption:String
    val trigered: Boolean
}