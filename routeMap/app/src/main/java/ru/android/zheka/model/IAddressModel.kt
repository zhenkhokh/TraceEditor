package ru.android.zheka.model

import android.widget.EditText
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import io.reactivex.functions.Consumer
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.OnFocusCallback

interface IAddressModel : IEnterPointModel {
    val region: ObservableField<String>
    val city: ObservableField<String>
    val street: ObservableField<String>
    val house: ObservableField<String>
    var clearButton: ObservableField<ButtonHandler>
    var recordButton: ObservableField<ButtonHandler>
    var backButton: ObservableField<ButtonHandler>
    var onFocusLost: OnFocusCallback
    var focusReg: ObservableBoolean
    val focusCity: ObservableBoolean
    val focusStreet: ObservableBoolean
    val focusHouse: ObservableBoolean
}

class FocusData (val lost:Consumer<in EditText>, val on:Consumer<in EditText>): OnFocusCallback {
    override fun onFocusLost(view: EditText) {
        lost.accept(view)
    }

    override fun onFocus(view: EditText) {
        on.accept(view)
    }
}
