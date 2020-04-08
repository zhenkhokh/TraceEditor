package ru.android.zheka.fragment;

import javax.inject.Inject;

import ru.android.zheka.coreUI.AbstractFragment;
import ru.android.zheka.gmapexample1.R;
import ru.android.zheka.gmapexample1.databinding.GeoFragmentBinding;
import ru.android.zheka.vm.IGeoVM;

public class Geo extends AbstractFragment <GeoFragmentBinding> implements IGeo {

    @Inject
    IGeoVM viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.geo_fragment;
    }

    @Override
    protected void initComponent() {

    }

    @Override
    protected void onInitBinding(GeoFragmentBinding binding) {
        binding.setVm (viewModel);
    }

    @Override
    protected void onResumeBinding(GeoFragmentBinding binding) {
        viewModel.onResume ();
    }

    @Override
    protected void onDestroyBinding(GeoFragmentBinding binding) {
        viewModel.onDestroy ();
    }
}
