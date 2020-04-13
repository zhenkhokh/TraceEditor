package ru.android.zheka.model;

import android.content.Context;

import ru.android.zheka.core.AktionMessage;
import ru.android.zheka.core.Message;
import ru.android.zheka.coreUI.PanelModel;

public class SettingsModel extends PanelModel implements ISettingsModel {
    public SettingsModel(Context view) {
        super (view);
    }

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
