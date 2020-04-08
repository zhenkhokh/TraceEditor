package ru.android.zheka.fragment;

import javax.inject.Inject;

import ru.android.zheka.coreUI.AbstractFragment;
import ru.android.zheka.gmapexample1.R;
import ru.android.zheka.gmapexample1.databinding.HomeFragmentBinding;
import ru.android.zheka.vm.IPanelHomeVM;

public class Home extends AbstractFragment <HomeFragmentBinding> implements IHome{

    @Inject
    public IPanelHomeVM viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.home_fragment;
    }

    @Override
    protected void initComponent() {

    }

    @Override
    protected void onInitBinding(HomeFragmentBinding binding) {
        binding.setModel (viewModel.model ());
    }

    @Override
    protected void onResumeBinding(HomeFragmentBinding binding) {
        viewModel.onResume ();
    }

    @Override
    protected void onDestroyBinding(HomeFragmentBinding binding) {
        viewModel.onDestroy ();
    }
}
