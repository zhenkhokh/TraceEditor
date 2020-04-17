package ru.android.zheka.fragment

import ru.android.zheka.coreUI.AbstractFragment
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.databinding.SettingsFragmentBinding
import ru.android.zheka.vm.ISettingsVM
import javax.inject.Inject

class Settings : AbstractFragment<SettingsFragmentBinding?>(), ISettings {
    @JvmField
    @Inject
    var viewModel: ISettingsVM? = null
    override fun getLayoutId(): Int {
        return R.layout.settings_fragment
    }

    override fun initComponent() {}
    protected override fun onInitBinding(binding: SettingsFragmentBinding) {
        binding.vm = viewModel
    }

    protected override fun onResumeBinding(binding: SettingsFragmentBinding) {
        viewModel!!.onResume()
    }

    protected override fun onDestroyBinding(binding: SettingsFragmentBinding) {
        viewModel!!.onDestroy()
    }
}