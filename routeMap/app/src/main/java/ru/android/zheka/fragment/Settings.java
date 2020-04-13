package ru.android.zheka.fragment;
import javax.inject.Inject;

import ru.android.zheka.coreUI.AbstractFragment;
import ru.android.zheka.gmapexample1.R;
import ru.android.zheka.gmapexample1.databinding.SettingsFragmentBinding;
import ru.android.zheka.vm.ISettingsVM;

public class Settings  extends AbstractFragment <SettingsFragmentBinding> implements   ISettings{
    @Inject
    ISettingsVM viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.settings_fragment;
    }

    @Override
    protected void initComponent() {

    }

    @Override
    protected void onInitBinding(SettingsFragmentBinding binding) {
        binding.setVm(viewModel);
    }

    @Override
    protected void onResumeBinding(SettingsFragmentBinding binding) {
        viewModel.onResume ();
    }

    @Override
    protected void onDestroyBinding(SettingsFragmentBinding binding) {
        viewModel.onDestroy ();
    }
}
