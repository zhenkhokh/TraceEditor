package ru.android.zheka.model;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import ru.android.zheka.core.IInfoModel;
import ru.android.zheka.coreUI.SpinnerHandler;

public interface ISettingsModel  extends IInfoModel {
    ObservableBoolean updateLen();
    ObservableBoolean optimizationNo();
    ObservableBoolean optimizationGoogle();
    ObservableBoolean optimizationBellmanFord();
    ObservableInt getSpeedTrace();
    ObservableField <SpinnerHandler> getSpinner();
}
