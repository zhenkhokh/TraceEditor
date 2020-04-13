package ru.android.zheka.vm

import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.model.ISettingsModel

class SettingsVM(var view: IActivity, var model: ISettingsModel) : ISettingsVM {
    override fun onResume() {
    }

    override fun model(): ISettingsModel? {
        return model
    }

    override fun onDestroy() {
    }

}
