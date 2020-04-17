package ru.android.zheka.fragment

import ru.android.zheka.coreUI.AbstractFragment
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.databinding.HomeFragmentBinding
import ru.android.zheka.vm.IPanelHomeVM
import javax.inject.Inject

class Home : AbstractFragment<HomeFragmentBinding?>(), IHome {
    @JvmField
    @Inject
    var viewModel: IPanelHomeVM? = null
    override fun getLayoutId(): Int {
        return R.layout.home_fragment
    }

    override fun initComponent() {}
    protected override fun onInitBinding(binding: HomeFragmentBinding) {
        binding.model = viewModel!!.model()
    }

    protected override fun onResumeBinding(binding: HomeFragmentBinding) {
        viewModel!!.onResume()
    }

    protected override fun onDestroyBinding(binding: HomeFragmentBinding) {
        viewModel!!.onDestroy()
    }
}