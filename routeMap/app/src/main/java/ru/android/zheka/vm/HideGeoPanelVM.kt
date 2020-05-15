package ru.android.zheka.vm

import android.view.View
import ru.android.zheka.db.Point
import ru.android.zheka.fragment.Geo
import ru.android.zheka.fragment.IHideGeo
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.LatLngModel

open class HideGeoPanelVM(view: IHideGeo, model: LatLngModel) : LatLngVM(view, model) {

    private val item: Point = Point()
    private val items = mutableListOf(item)
    init {
        item.name = view.activity.resources.getString(R.string.hide_panel_expand)
    }

    override val points: MutableList<Point>
        get() = items

    override val onClickListener: View.OnClickListener?
        get() = View.OnClickListener {
            view.switchToFragment(R.id.geoFragment, Geo())
        }

    override fun onDestroy() {
        super.onDestroy()
        items.removeAll(items)
    }

    override fun onResume() {
        super.onResume()
        model.hideTitle.set(View.GONE)
    }
}
