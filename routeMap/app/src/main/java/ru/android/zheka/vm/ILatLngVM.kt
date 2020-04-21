package ru.android.zheka.vm

import android.content.Context
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.coreUI.IVM
import ru.android.zheka.model.ILatLngModel

interface ILatLngVM : IVM<ILatLngModel> {
    val shownItems: List<String>
    val context: Context
    val view: IActivity
}
