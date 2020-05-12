package ru.android.zheka.vm

import android.content.Context
import android.view.View
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.coreUI.IVM
import ru.android.zheka.model.ILatLngModel

interface ILatLngVM : IVM<ILatLngModel> {
    val onClickListener: View.OnClickListener?
    val shownItems: MutableList<String>
    val context: Context
    val view: IActivity
}
