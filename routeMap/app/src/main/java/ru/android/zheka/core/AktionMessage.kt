package ru.android.zheka.core

class AktionMessage(msg: Enum<*>, vararg params: Any) {
    val params: Array<out Any>

    val msg: Enum<*>

    init {
        this.params = params
        this.msg = msg
    }
}