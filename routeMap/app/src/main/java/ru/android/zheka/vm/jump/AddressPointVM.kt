package ru.android.zheka.vm.jump

import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.fragment.EnterPoint
import ru.android.zheka.fragment.IEnterPoint
import ru.android.zheka.model.IAddressModel

class AddressPointVM(val view: IEnterPoint, val model: IAddressModel) : IAddressPointVM {

    override lateinit var panelModel: IPanelModel
    override fun options(): List<String> {
        TODO("Not yet implemented")
    }

    override fun switchFragment(fragment: EnterPoint, option: String) {
        TODO("Not yet implemented")
    }

    override fun onClick() {
        TODO("Not yet implemented")
    }

    override fun onResume() {
        TODO("Not yet implemented")
    }

    override fun model(): IAddressModel {
        return model
    }

    override fun onDestroy() {
        TODO("Not yet implemented")
    }

}
