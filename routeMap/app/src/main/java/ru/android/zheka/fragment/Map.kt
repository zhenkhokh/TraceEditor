package ru.android.zheka.fragment

import ru.android.zheka.coreUI.AbstractFragment
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.databinding.MapFragmentBinding
import ru.android.zheka.vm.IMapVM
import javax.inject.Inject

class Map : AbstractFragment<MapFragmentBinding>() {
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
}