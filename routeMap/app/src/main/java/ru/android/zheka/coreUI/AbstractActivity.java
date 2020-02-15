package ru.android.zheka.coreUI;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public abstract class AbstractActivity<B extends ViewDataBinding> extends
        AppCompatActivity
        //RoboFragmentActivity
        implements IActivity {


    private ErrorControl error;
    private B binding;

    protected abstract int getLayoutId();

    protected abstract void initComponent();

    protected abstract void onInitBinding(B binding);

    protected abstract void onResumeBinding(B binding);

    protected abstract void onDestroyBinding(B binding);

    @Override
    protected void onCreate(Bundle savedState) {
        super.onCreate (savedState);
        error = new ErrorControl (this);
        binding = DataBindingUtil.setContentView (this, getLayoutId ());
    }

    @Override
    protected void onStart() {
        super.onStart ();
        initComponent ();
        onInitBinding (binding);
    }

    @Override
    protected void onResume() {
        super.onResume ();
        onResumeBinding (binding);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy ();
        onDestroyBinding (binding);
        binding.unbind ();
    }

    @Override
    public void showError(Throwable throwable) {
        error.showError (throwable, a -> {
        });
    }

    @Override
    public Context getContext() {
        return this;
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
        return getSupportFragmentManager ();
    }
}
