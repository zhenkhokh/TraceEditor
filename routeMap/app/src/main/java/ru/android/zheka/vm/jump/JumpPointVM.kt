package ru.android.zheka.vm.jump

import android.content.Intent
import android.view.View
import ru.android.zheka.fragment.IJumpPoint
import ru.android.zheka.gmapexample1.GeoPositionActivity
import ru.android.zheka.gmapexample1.PositionInterceptor
import ru.android.zheka.model.AddressModel
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.EditVM

class JumpPointVM(view :IJumpPoint, model: LatLngModel) : EditVM(view, model), IJumpPointVM {
    private val points = AddressModel.geoCoder
            .points
            .toMutableList()
    private val names = AddressModel.geoCoder.adresses.toMutableList()

    override val shownItems: MutableList<String>
        get() = names

    override fun onClick(pos: Int) {
        val positionInterceptor = PositionInterceptor(view.activity)
        positionInterceptor.centerPosition = points[pos]
        val intent = positionInterceptor.newIntent
        intent.setClass(view.context(), GeoPositionActivity::class.java)
        intent.setAction(Intent.ACTION_VIEW)
        view.activity.startActivity(intent)
        view.activity.finish()
    }

    override fun onResume() {
        super.onResume()
        panelModel.action().set("Выберете адрес из списка")
        panelModel.inputVisible().set(View.GONE)
    }

    override fun onDestroy() {
        super.onDestroy()
        names.removeAll(names)
    }
}
