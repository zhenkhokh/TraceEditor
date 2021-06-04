package ru.android.zheka.coreUI

import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import ru.android.zheka.core.IInfoModel

open interface IPanelModel {
    fun keyListener(): ObservableField<View.OnKeyListener>
    fun input(): ObservableField<String>
    fun success(): ObservableField<String> // can be translated
    fun action(): ObservableField<String> // can be translated
    fun progressBarVisibility(): ObservableInt
    fun progressBarVisible()
    fun progressBarInvisible()
    fun progressBarGone()
    fun successColor(): ObservableInt
    fun successImage(): ObservableField<Drawable>
    val startButton: ObservableField<ButtonHandler>
    val stopButton: ObservableField<ButtonHandler>
    val nextButton: ObservableField<ButtonHandler>
    val startButton1: ObservableField<ButtonHandler>
    val stopButton1: ObservableField<ButtonHandler>
    val nextButton1: ObservableField<ButtonHandler>
    val startButton2: ObservableField<ButtonHandler>
    val stopButton2: ObservableField<ButtonHandler>
    val nextButton2: ObservableField<ButtonHandler>
    val spinner: ObservableField<SpinnerHandler>
    fun inputVisible(): ObservableInt
    fun update(model: IInfoModel) //    void createInputKeyListener(Consumer enter, IActivity view);

    companion object {
        const val COMBO_BOX_VISIBLE=0
        const val INPUT_TEXT_VISIBLE=1
        val EMPTY_SPINNER: ObservableField<SpinnerHandler> = ObservableField<SpinnerHandler>(SpinnerHandler())
    }
}