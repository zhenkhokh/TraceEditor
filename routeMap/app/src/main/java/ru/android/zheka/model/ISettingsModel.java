package ru.android.zheka.model;

import androidx.databinding.ObservableBoolean;
import ru.android.zheka.core.IInfoModel;

public interface ISettingsModel  extends IInfoModel {
    ObservableBoolean updateLen();
    ObservableBoolean optimizationNo();
    ObservableBoolean optimizationGoogle();
    ObservableBoolean optimizationBellmanFord();
}
