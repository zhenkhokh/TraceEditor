package ru.android.zheka.vm.jump

import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.coreUI.IVM
import ru.android.zheka.fragment.EnterPoint
import ru.android.zheka.model.IAddressModel

interface ICoordinatePointVM : IVM<IAddressModel> {
    var panelModel: IPanelModel
    fun options(): List<String>
    fun switchFragment(fragment: EnterPoint, option: String)
    fun onClick()
}
