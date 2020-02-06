package ru.android.zheka.coreUI;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public abstract class AbstractFragment<B extends ViewDataBinding> extends Fragment implements IBaseFragment {
    private ErrorControl error;
    private B binding;

    protected abstract int getLayoutId();

    protected abstract void initComponent();

    protected abstract void onInitBinding(B binding);

    protected abstract void onResumeBinding(B binding);

    protected abstract void onDestroyBinding(B binding);

    @Override
    public void onStart() {
        super.onStart ();
        initComponent ();
        onInitBinding (binding);
    }

    @Override
    public void onResume() {
        super.onResume ();
        onResumeBinding (binding);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView ();
        onDestroyBinding (binding);
        binding.unbind ();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        error = new ErrorControl (this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstance) {
        binding = DataBindingUtil.inflate (inflater, getLayoutId (), container, false);
        return binding.getRoot ();
    }

    public void showError(Throwable throwable) {
        error.showError (throwable, a -> {
        });
    }

    public Context getContext() {
        return getActivity ();
    }

    @Override
    public void switchToFragment(int fragmentId, @NonNull Fragment fragment) {
        FragmentTransaction transaction = getManager ().beginTransaction ();
        transaction.replace (fragmentId, fragment);
        transaction.commit ();
    }

    @NonNull
    @Override
    public FragmentManager getManager() {
        return getActivity ().getSupportFragmentManager ();
    }

}
