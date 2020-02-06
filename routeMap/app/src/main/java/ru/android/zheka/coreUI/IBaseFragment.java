package ru.android.zheka.coreUI;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public interface IBaseFragment extends IActivity{
    void showError(Throwable throwable);

    void switchToFragment(int fragmentId, @NonNull Fragment fragment);

    FragmentManager getManager();

//    void nextView() throws InstantiationException, IllegalAccessException;
//    void net
}
