package ru.android.zheka.vm

import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.model.IMapModel

class MapVM(var view: IActivity, var model: IMapModel) : IMapVM {
    override fun onResume() {}
    override fun onDestroy() {}
    override fun model(): IMapModel {
        return model
    }

}