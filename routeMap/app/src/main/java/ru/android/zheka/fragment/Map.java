package ru.android.zheka.fragment;

import javax.inject.Inject;

import ru.android.zheka.coreUI.AbstractFragment;
import ru.android.zheka.gmapexample1.R;
import ru.android.zheka.gmapexample1.databinding.MapFragmentBinding;
import ru.android.zheka.vm.IMapVM;

public class Map extends AbstractFragment <MapFragmentBinding> {
    @Inject
    IMapVM viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.map_fragment;
    }

    @Override
    protected void initComponent() {

    }

    @Override
    protected void onInitBinding(MapFragmentBinding binding) {
        binding.setVm (viewModel);
    }

    @Override
    protected void onResumeBinding(MapFragmentBinding binding) {
        viewModel.onResume ();
    }

    @Override
    protected void onDestroyBinding(MapFragmentBinding binding) {
        viewModel.onDestroy ();
    }
}
