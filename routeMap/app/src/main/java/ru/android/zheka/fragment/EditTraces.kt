package ru.android.zheka.fragment

import ru.android.zheka.gmapexample1.databinding.LatLngFragmentBinding
import ru.android.zheka.vm.IEditTracesVM
import ru.android.zheka.vm.IEditVM
import javax.inject.Inject

class EditTraces : Edit(), IEditTraces {
    @Inject
    lateinit var viewModel_: IEditTracesVM

    override fun initAdapter(binding: LatLngFragmentBinding): LatLngFragmentBinding {
        viewModel = viewModel_ as IEditVM
        return super.initAdapter(binding)
    }
}