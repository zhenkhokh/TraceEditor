package ru.android.zheka.coreUI;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public interface IActivity {
    Activity getActivity();

    void showError(Throwable throwable);

    Context getContext();

    void switchToFragment(int fragmentId, @NonNull Fragment fragment);

    void removeFragment(@NonNull Fragment fragment);

    FragmentManager getManager();
}
