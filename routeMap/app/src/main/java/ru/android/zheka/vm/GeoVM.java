package ru.android.zheka.vm;

import ru.android.zheka.coreUI.IActivity;
import ru.android.zheka.model.IGeoModel;

public class GeoVM implements IGeoVM {
    IActivity view;
    IGeoModel model;

    public GeoVM(IActivity view, IGeoModel model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void home() {
//TODO

    }

    @Override
    public void points() {
//TODO

    }

    @Override
    public void saveTrace() {
//TODO

    }

    @Override
    public void map() {
//TODO

    }

    @Override
    public void addWayPoints() {
//TODO
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public IGeoModel model() {
        return model;
    }
}
