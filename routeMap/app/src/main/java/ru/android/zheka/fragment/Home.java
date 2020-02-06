package ru.android.zheka.fragment;

import javax.inject.Inject;

import ru.android.zheka.gmapexample1.databinding.HomeFragmentBinding;
import ru.android.zheka.coreUI.AbstractFragment;
import ru.android.zheka.gmapexample1.R;
import ru.android.zheka.vm.IPanelHomeVM;
import ru.android.zheka.vm.PanelHomeVM;

public final class Home extends AbstractFragment <HomeFragmentBinding> {

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
        binding.setVm (viewModel);
    }

    @Override
    protected void onResumeBinding(HomeFragmentBinding binding) {
        binding.getVm ().onResume ();
    }

    @Override
    protected void onDestroyBinding(HomeFragmentBinding binding) {
        binding.getVm ().onDestroy ();
    }
}
