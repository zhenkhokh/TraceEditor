package ru.android.zheka.fragment

import kotlinx.android.synthetic.main.home_fragment.view.*
import ru.android.zheka.coreUI.AbstractFragment
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.databinding.MapFragmentBinding
import ru.android.zheka.vm.IMapVM
import javax.inject.Inject

class Map : AbstractFragment<MapFragmentBinding>(), IActivity {
    @JvmField
    @Inject
    var viewModel: IMapVM? = null
    override
    val layoutId = R.layout.map_fragment

    override fun initComponent() {}
    override fun onInitBinding(binding: MapFragmentBinding) {
        binding.vm = viewModel
    }

    override fun onResumeBinding(binding: MapFragmentBinding) {
        viewModel!!.onResume()
    }

    override fun onDestroyBinding(binding: MapFragmentBinding) {
        viewModel!!.onDestroy()
    }

    override fun initAdapter(binding: MapFragmentBinding): MapFragmentBinding {
        val scale = context?.resources?.displayMetrics?.density
        var value: Int = ((scale ?: 0F).times(Geo.sp.toFloat())).toInt()
        value = if (value == 0) 230 else value
        binding.root.baseScanBarcode_background?.layoutParams?.height = value
        return binding
    }
}