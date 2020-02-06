package ru.android.zheka.vm;

import ru.android.zheka.coreUI.IVM;
import ru.android.zheka.model.IHomeModel;

public interface IPanelHomeVM extends IVM <IHomeModel> {
    void info();

    void address();

    void geo();

    void editPoints();

    void editTraces();

    void pointNavigate();

    void createTrace();

    void settingsAction();
}
