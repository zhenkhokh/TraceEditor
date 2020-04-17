package ru.android.zheka.fragment

import ru.android.zheka.coreUI.AbstractFragment
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.databinding.SettingsFragmentBinding
import ru.android.zheka.vm.ISettingsVM
import javax.inject.Inject

class Settings : AbstractFragment<SettingsFragmentBinding>(), ISettings {
    @JvmField
    @Inject
    var viewModel: ISettingsVM? = null
    override
    val layoutId = R.layout.settings_fragment

    override fun initComponent() {}
    override fun onInitBinding(binding: SettingsFragmentBinding) {
        binding.vm = viewModel
    }

    override fun onResumeBinding(binding: SettingsFragmentBinding) {
        viewModel!!.onResume()
    }

    override fun onDestroyBinding(binding: SettingsFragmentBinding) {
        viewModel!!.onDestroy()
    }
}