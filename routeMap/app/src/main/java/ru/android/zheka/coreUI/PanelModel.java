package ru.android.zheka.coreUI;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import ru.android.zheka.core.IInfoModel;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class PanelModel implements IPanelModel {
    @BindingAdapter("android:visibility")
    public static void setVisibility(View view, Boolean value) {
        view.setVisibility(value ? View.VISIBLE : View.GONE);
    }
    private final ObservableInt progressBarVisibility;
    private final ObservableField action;
    private final ObservableField success;
    private final ObservableField <String> input;
    private final ObservableField <View.OnKeyListener> keyListener;
    private final ObservableField <Drawable> successImage;
    //    private final ObservableInt                       successColor;
    private final Resources resources;
    private final ObservableInt inputVisibility;
    private final List <ObservableField <ButtonHandler>> startButton = new ArrayList <> ();
    private final List <ObservableField <ButtonHandler>> stopButton = new ArrayList <> ();
    private final List <ObservableField <ButtonHandler>> nextButton = new ArrayList <> ();

    @Override
    public ObservableField <View.OnKeyListener> keyListener() {
        return keyListener;
    }

    public PanelModel(Context view) {
        resources = view.getResources ();
        input = new ObservableField <> ("");
        keyListener = new ObservableField <> ();
        success = new ObservableField ();
//        successColor = new ObservableInt(R.color.green);
        action = new ObservableField ();
        progressBarVisibility = new ObservableInt (INVISIBLE);
        successImage = new ObservableField <> ();
        inputVisibility = new ObservableInt (INPUT_TEXT_VISIBLE);
        for (int i = 0; i < 3; i++) {
            startButton.add (new ObservableField <> (new ButtonHandler ()));
            stopButton.add (new ObservableField <> (new ButtonHandler ()));
            nextButton.add (new ObservableField <> (new ButtonHandler ()));
        }
    }

    @Override
    public ObservableField <String> input() {
        return input;
    }

    @Override
    public ObservableField <String> success() {
        return success;
    }

    @Override
    public ObservableField <String> action() {
        return action;
    }

    @Override
    public ObservableInt progressBarVisibility() {
        return progressBarVisibility;
    }

    @Override
    public void progressBarVisible() {
        progressBarVisibility.set (VISIBLE);
    }

    @Override
    public void progressBarInvisible() {
        progressBarVisibility.set (INVISIBLE);
    }

    @Override
    public void progressBarGone() {
        progressBarVisibility.set (GONE);
    }

    @Override
    public ObservableInt successColor() {
        return null;//successColor;
    }


    @Override
    public ObservableField <Drawable> successImage() {
        return successImage;
    }

    @Override
    public void update(IInfoModel model) {
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

    @Override
    public ObservableInt inputVisible() {
        return inputVisibility;
    }

    @Override
    public ObservableField <ButtonHandler> getStartButton() {
        return startButton.get (0);
    }

    @Override
    public ObservableField <ButtonHandler> getStopButton() {
        return stopButton.get (0);
    }

    @Override
    public ObservableField <ButtonHandler> getNextButton() {
        return nextButton.get (0);
    }

    @Override
    public ObservableField <ButtonHandler> getStartButton1() {
        return startButton.get (1);
    }

    @Override
    public ObservableField <ButtonHandler> getStopButton1() {
        return stopButton.get (1);
    }

    @Override
    public ObservableField <ButtonHandler> getNextButton1() {
        return nextButton.get (1);
    }

    @Override
    public ObservableField <ButtonHandler> getStartButton2() {
        return startButton.get (2);
    }

    @Override
    public ObservableField <ButtonHandler> getStopButton2() {
        return stopButton.get (2);
    }

    @Override
    public ObservableField <ButtonHandler> getNextButton2() {
        return nextButton.get (2);
    }

    @Override
    public ObservableField <SpinnerHandler> getSpinner() {
        return EMPTY_SPINNER;
    }

//    protected Drawable getDrawable(int id) {
//        return id == 0
//                ? null
//                : getResources().getDrawable(id, null);
//    }

    private Resources getResources() {
        return resources;
    }

//    @Override
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
}
