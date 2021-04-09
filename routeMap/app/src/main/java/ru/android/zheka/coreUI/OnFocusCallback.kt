package ru.android.zheka.coreUI

import android.widget.EditText

interface OnFocusCallback {
    fun onFocusLost(view: EditText)
    fun onFocus(view: EditText)
}
