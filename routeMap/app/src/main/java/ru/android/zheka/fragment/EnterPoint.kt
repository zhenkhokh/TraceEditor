package ru.android.zheka.fragment

import ru.android.zheka.coreUI.AbstractFragment
import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.gmapexample1.databinding.EnterPointBinding
import ru.android.zheka.model.IEnterPointModel
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.jump.IAddressPointVM
import ru.android.zheka.vm.jump.ICoordinatePointVM
import javax.inject.Inject

class EnterPoint : AbstractFragment<EnterPointBinding>(), IEnterPoint{

    lateinit var viewModel: ICoordinatePointVM

    @Inject
    lateinit var viewModelCoordinate: ICoordinatePointVM

    @Inject
    lateinit var viewModelAddress: IAddressPointVM

    @Inject
    lateinit var panelModel: IPanelModel

    @Inject
    lateinit var latLngModel: LatLngModel

    override val layoutId: Int
        get() = R.layout.enter_point

    override fun initComponent() {
        val res = activity?.resources
        val model = viewModelAddress.model()
        val raw = res?.getString(R.string.enter_point_address)
        if (raw.equals(model.spinnerOptions)) {
            viewModel = viewModelAddress
            return
        }
        viewModel = viewModelCoordinate
    }

    override fun onInitBinding(binding: EnterPointBinding) {
        viewModel.panelModel = panelModel
        binding.vm = viewModel
        binding.staticSpace = IEnterPointModel.Companion
    }

    override fun onResumeBinding(binding: EnterPointBinding) {
        viewModel.onResume()
        latLngModel.custom = true
        latLngModel._customPoints -= latLngModel._customPoints
    }

    override fun onDestroyBinding(binding: EnterPointBinding) {
        viewModel.onDestroy()
    }
}
