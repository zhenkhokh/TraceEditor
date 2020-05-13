package ru.android.zheka.fragment

import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.gmapexample1.databinding.LatLngFragmentBinding
import ru.android.zheka.vm.jump.IJumpPointVM
import javax.inject.Inject

class JumpPoint : Edit(), IJumpPoint {
    @Inject
    lateinit var viewModelJump: IJumpPointVM

    @Inject
    override lateinit var panelModel: IPanelModel

    override fun initAdapter(binding: LatLngFragmentBinding): LatLngFragmentBinding {
        viewModel = viewModelJump
        return super.initAdapter(binding)
    }
}
