package ru.android.zheka.fragment

import ru.android.zheka.coreUI.AbstractFragment
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.databinding.HomeFragmentBinding
import ru.android.zheka.vm.IPanelHomeVM
import javax.inject.Inject

class Home : AbstractFragment<HomeFragmentBinding>(), IHome {
    @JvmField
    @Inject
    var viewModel: IPanelHomeVM? = null
    override
    val layoutId = R.layout.home_fragment

    override fun initComponent() {}
    override fun onInitBinding(binding: HomeFragmentBinding) {
        binding.model = viewModel!!.model()
    }

    override fun onResumeBinding(binding: HomeFragmentBinding) {
        viewModel!!.onResume()
    }

    override fun onDestroyBinding(binding: HomeFragmentBinding) {
        viewModel!!.onDestroy()
    }
}