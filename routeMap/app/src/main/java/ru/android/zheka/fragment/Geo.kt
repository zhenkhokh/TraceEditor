package ru.android.zheka.fragment

import ru.android.zheka.coreUI.AbstractFragment
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.databinding.GeoFragmentBinding
import ru.android.zheka.vm.IGeoVM
import javax.inject.Inject

class Geo : AbstractFragment<GeoFragmentBinding?>(), IGeo {
    @JvmField
    @Inject
    var viewModel: IGeoVM? = null
    override fun getLayoutId(): Int {
        return R.layout.geo_fragment
    }

    override fun initComponent() {}
    protected override fun onInitBinding(binding: GeoFragmentBinding) {
        binding.vm = viewModel
    }

    protected override fun onResumeBinding(binding: GeoFragmentBinding) {
        viewModel!!.onResume()
    }

    protected override fun onDestroyBinding(binding: GeoFragmentBinding) {
        viewModel!!.onDestroy()
    }
}