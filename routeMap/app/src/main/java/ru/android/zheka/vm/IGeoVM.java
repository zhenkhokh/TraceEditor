package ru.android.zheka.vm;

import ru.android.zheka.coreUI.IVM;
import ru.android.zheka.model.IGeoModel;

public interface IGeoVM extends IVM<IGeoModel> {
    void home();
    void points();
    void saveTrace();
    void map();
    void addWayPoints();
}
