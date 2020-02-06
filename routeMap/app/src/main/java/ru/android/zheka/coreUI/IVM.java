package ru.android.zheka.coreUI;

public interface IVM<Model> {
    void onResume();

    void onDestroy();

    Model model();
}
