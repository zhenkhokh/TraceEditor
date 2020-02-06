package ru.android.zheka.coreUI;

import android.view.View;

import androidx.databinding.ObservableInt;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class ButtonHandler {
    private String        label;
    private ObservableInt visible;
    private Consumer      methodHandler;
    private IActivity view;
    // only one listener must be for one button, this works in generaly

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;// always use constructor before. Observable field does not effective here
    }

    public ObservableInt getVisible() {
        return visible;
    }

    public void onClick() {
        Observable.just(true).subscribe(methodHandler, view::showError).dispose();
    }

    public ButtonHandler(Consumer<Boolean> methodHandler, int idLabel, IActivity view) {
        this.label = view.getContext().getString(idLabel);
        this.visible = new ObservableInt(View.VISIBLE);
        this.methodHandler = methodHandler;
        this.view = view;
    }

    public ButtonHandler() {
        this.visible = new ObservableInt(View.INVISIBLE);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        visible.set(View.GONE);
    }
}
