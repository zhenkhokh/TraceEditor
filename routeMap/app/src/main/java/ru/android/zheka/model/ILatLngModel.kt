package ru.android.zheka.model

import androidx.databinding.ObservableField
import ru.android.zheka.db.Point

interface ILatLngModel {
    fun titleText(): ObservableField<String>
    val points: List<Point>
    var chekedVisibility: Int
    var checked: ArrayList<Boolean>
    var spinnerOption:String
    val trigered: Boolean
    var _customPoints: List<Point>?
}