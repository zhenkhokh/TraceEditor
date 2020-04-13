package ru.android.zheka.coreUI;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import ru.android.zheka.core.IInfoModel;

public interface IPanelModel {
    int COMBO_BOX_VISIBLE = 0;
    int INPUT_TEXT_VISIBLE = 1;

    ObservableField <ButtonHandler>
            INVISIBLE_BUTTON = new ObservableField <> (new ButtonHandler ());
    ObservableField <SpinnerHandler>
            EMPTY_SPINNER = new ObservableField <> (new SpinnerHandler ());

    ObservableField <View.OnKeyListener> keyListener();

    ObservableField <String> input();

    ObservableField success();// can be translated

    ObservableField action();// can be translated

    ObservableInt progressBarVisibility();

    void progressBarVisible();

    void progressBarInvisible();

    void progressBarGone();

    ObservableInt successColor();

    ObservableField <Drawable> successImage();

    ObservableField <ButtonHandler> getStartButton();

    ObservableField <ButtonHandler> getStopButton();

    ObservableField <ButtonHandler> getNextButton();

    ObservableField <ButtonHandler> getStartButton1();

    ObservableField <ButtonHandler> getStopButton1();

    ObservableField <ButtonHandler> getNextButton1();

    ObservableField <ButtonHandler> getStartButton2();

    ObservableField <ButtonHandler> getStopButton2();

    ObservableField <ButtonHandler> getNextButton2();

    ObservableField <SpinnerHandler> getSpinner();

    ObservableInt inputVisible();

    void update(IInfoModel model);

//    void createInputKeyListener(Consumer enter, IActivity view);
}
