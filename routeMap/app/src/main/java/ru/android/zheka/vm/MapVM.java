package ru.android.zheka.vm;

import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.model.IMapModel;

public class MapVM implements IMapVM {

    IActivity view;
    IMapModel model;

    public MapVM(IActivity view, IMapModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public IMapModel model() {
        return model;
    }
}
