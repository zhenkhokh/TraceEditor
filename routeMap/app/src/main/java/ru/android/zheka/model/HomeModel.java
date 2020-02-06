package ru.android.zheka.model;

import androidx.databinding.ObservableField;
import ru.android.zheka.core.AktionMessage;
import ru.android.zheka.core.Message;
import ru.android.zheka.coreUI.ButtonHandler;
import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.coreUI.PanelModel;

public class HomeModel extends PanelModel implements IHomeModel {
    private ObservableField <ButtonHandler> startButton;

    public HomeModel(IActivity view) {
        super (view.getContext ());
    }

//    @Override
//    public void setStartButton(ObservableField button) {
//        this.startButton = button;
//    }
//
//    @Override
//    public ObservableField <ButtonHandler> getStartButton() {
//        return this.startButton;
//    }

    @Override
    public String getInput() {
        return null;
    }

    @Override
    public AktionMessage getAction() {
        return null;
    }

    @Override
    public Message getMessage() {
        return null;
    }
}
