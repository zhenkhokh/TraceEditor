package ru.android.zheka.vm

import android.view.View
import ru.android.zheka.fragment.Geo
import ru.android.zheka.fragment.IHideMap
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.fragment.Map


class HideMapPanelVM(view: IHideMap, model: LatLngModel) : HideGeoPanelVM(view, model) {
    override val onClickListener: View.OnClickListener?
        get() = View.OnClickListener {
            view.switchToFragment(R.id.mapFragment, Map())
        }
}
