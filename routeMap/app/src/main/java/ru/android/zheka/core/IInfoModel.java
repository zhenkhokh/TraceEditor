package ru.android.zheka.core;

import ru.android.zheka.coreUI.IPanelModel;

public interface IInfoModel extends IPanelModel {
    String getInput();

    AktionMessage getAction();

    Message getMessage();

}
