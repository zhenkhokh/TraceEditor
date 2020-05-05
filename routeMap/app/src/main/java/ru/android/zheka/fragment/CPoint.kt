package ru.android.zheka.fragment

import ru.android.zheka.gmapexample1.databinding.LatLngFragmentBinding
import ru.android.zheka.vm.ICPointVM
import ru.android.zheka.vm.IEditVM
import javax.inject.Inject

class CPoint : Edit(), ICPoint {
    @Inject
    lateinit var viewModel_: ICPointVM

    override fun initAdapter(binding: LatLngFragmentBinding): LatLngFragmentBinding {
        viewModel = viewModel_ as IEditVM
        return super.initAdapter(binding)
    }
}
