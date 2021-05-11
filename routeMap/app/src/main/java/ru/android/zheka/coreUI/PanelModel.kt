package ru.android.zheka.coreUI

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.View
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import ru.android.zheka.core.IInfoModel
import ru.android.zheka.gmapexample1.R

open class PanelModel(view: Context?) : IPanelModel {
    private val progressBarVisibility: ObservableInt
    private val action: ObservableField<String>
    private val success: ObservableField<String>
    private val input: ObservableField<String>
    private val keyListener: ObservableField<View.OnKeyListener>
    private val successImage: ObservableField<Drawable>
    private val successColor: ObservableInt

    //    protected Drawable getDrawable(int id) {
    //        return id == 0
    //                ? null
    //                : getResources().getDrawable(id, null);
    //    }
    //    @Override
    //    private final ObservableInt                       successColor;
    private val resources: Resources
    private val inputVisibility: ObservableInt
    private val _startButton: MutableList<ObservableField<ButtonHandler>> = ArrayList()
    private val _stopButton: MutableList<ObservableField<ButtonHandler>> = ArrayList()
    private val _nextButton: MutableList<ObservableField<ButtonHandler>> = ArrayList()
    override fun keyListener(): ObservableField<View.OnKeyListener> {
        return keyListener
    }

    override fun input(): ObservableField<String> {
        return input
    }

    override fun success(): ObservableField<String> {
        return success
    }

    override fun action(): ObservableField<String> {
        return action
    }

    override fun progressBarVisibility(): ObservableInt {
        return progressBarVisibility
    }

    override fun progressBarVisible() {
        progressBarVisibility.set(View.VISIBLE)
    }

    override fun progressBarInvisible() {
        progressBarVisibility.set(View.INVISIBLE)
    }

    override fun progressBarGone() {
        progressBarVisibility.set(View.GONE)
    }

    override fun successColor(): ObservableInt {
        return successColor
    }

    override fun successImage(): ObservableField<Drawable> {
        return successImage
    }

    override fun update(model: IInfoModel) {
//        d("DEBUG", "BaseScanBarcodeModel::update() called with: model = [" + model + "]");
//        action.setTranslated(model.getAction().getMsg(), model.getAction().getParams());
//        if (model.getMessage() instanceof ExceptionMessage) {
//            success.setTranslatedError(((ExceptionMessage) model.getMessage()).getException());
//            input.set(null);
//        } else {
//            message = (ValidationMessage) model.getMessage();
//            success.setTranslated(message.getMsg(), message.getParams());
//            input.set(model.getInput());
//        }
//        successColor.set(message.getColorId());
//        successImage.set(getDrawable(message.getImageId()));
    }

    override fun inputVisible(): ObservableInt {
        return inputVisibility
    }

    override val startButton: ObservableField<ButtonHandler>
        get()=_startButton[0]

    override val stopButton: ObservableField<ButtonHandler>
        get() = _stopButton[0]

    override val nextButton: ObservableField<ButtonHandler>
        get()=_nextButton[0]

    override val startButton1: ObservableField<ButtonHandler>
        get()=_startButton[1]

    override val stopButton1: ObservableField<ButtonHandler>
        get()=_stopButton[1]

    override val nextButton1: ObservableField<ButtonHandler>
        get()=_nextButton[1]

    override val startButton2: ObservableField<ButtonHandler>
        get()=_startButton[2]

    override val stopButton2: ObservableField<ButtonHandler>
        get()=_stopButton[2]

    override val nextButton2: ObservableField<ButtonHandler>
        get()=_nextButton[2]

    override val spinner: ObservableField<SpinnerHandler>
        get()=IPanelModel.EMPTY_SPINNER

    //    public void createInputKeyListener(Consumer enter,
    //                                       IActivity view) {
    //        keyListener.set((v, keyCode, event) -> {
    //            if (event.getAction() != KeyEvent.ACTION_DOWN) { return false; }
    //            if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
    //                Observable.just(true).subscribe(enter, view::showError).dispose();
    //            }
    //            if (event.getKeyCode() == this.getStartButton().get().getKeyCode()) {
    //                this.getStartButton().get().onClick();
    //            }
    //            if (event.getKeyCode() == this.getStopButton().get().getKeyCode()) {
    //                this.getStopButton().get().onClick();
    //            }
    //            if (event.getKeyCode() == this.getNextButton().get().getKeyCode()) {
    //                this.getNextButton().get().onClick();
    //            }
    //            return false;
    //        });
    //    }
    companion object {
        @JvmStatic
        @BindingAdapter("android:visibility")
        fun setVisibility(view: View, value: Boolean) {
            view.visibility=if (value) View.VISIBLE else View.GONE
        }
    }

    init {
        resources=view!!.resources
        input=ObservableField("")
        keyListener=ObservableField()
        success=ObservableField<String>("")
        successColor = ObservableInt(R.color.white)
        action=ObservableField<String>("")
        progressBarVisibility=ObservableInt(View.INVISIBLE)
        successImage=ObservableField()
        inputVisibility=ObservableInt(IPanelModel.INPUT_TEXT_VISIBLE)
        for (i in 0..2) {
            _startButton.add(ObservableField(ButtonHandler()))
            _stopButton.add(ObservableField(ButtonHandler()))
            _nextButton.add(ObservableField(ButtonHandler()))
        }
    }
}