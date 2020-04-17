package ru.android.zheka.fragment

import ru.android.zheka.coreUI.AbstractFragment
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.databinding.GeoFragmentBinding
import ru.android.zheka.vm.IGeoVM
import javax.inject.Inject

class Geo : AbstractFragment<GeoFragmentBinding>(), IGeo {
    @JvmField
    @Inject
    var viewModel: IGeoVM? = null

    override
    val layoutId =  R.layout.geo_fragment;

    override fun initComponent() {}
    override fun onInitBinding(binding: GeoFragmentBinding) {
        binding!!.vm = viewModel
    }

    override fun onResumeBinding(binding: GeoFragmentBinding) {
        viewModel!!.onResume()
    }

    override fun onDestroyBinding(binding: GeoFragmentBinding) {
        viewModel!!.onDestroy()
    }
}