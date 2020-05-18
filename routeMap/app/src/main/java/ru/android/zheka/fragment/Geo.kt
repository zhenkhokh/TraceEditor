package ru.android.zheka.fragment

import kotlinx.android.synthetic.main.home_fragment.view.*
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

    companion object {
        const val sp = 110
    }

    override fun initAdapter(binding: GeoFragmentBinding): GeoFragmentBinding {
        if (viewModel?.model()?.config?.offline?.toBoolean()?:false) {
            val scale = context?.resources?.displayMetrics?.density
            var value: Int = ((scale ?: 0F).times(sp.toFloat())).toInt()
            value = if (value == 0) 230 else value
            binding.root.baseScanBarcode_background?.layoutParams?.height = value
        }
        return binding
    }
}