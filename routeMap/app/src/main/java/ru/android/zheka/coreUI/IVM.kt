package ru.android.zheka.coreUI

interface IVM<Model> {
    fun onResume()
    fun onDestroy()
    fun model(): Model
}