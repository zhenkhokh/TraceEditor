package ru.android.zheka.vm

import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.IMapModel

class MapVM(var view: IActivity, var model: IMapModel) : IMapVM {
    override fun geo() {
        model.actvity.goPosition(false)
    }

    override fun onResume() {
        model.startButton.set(ButtonHandler({ geo() }, R.string.map_goPosition, view))
    }

    override fun onDestroy() {}
    override fun model(): IMapModel {
        return model
    }

}