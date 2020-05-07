package ru.android.zheka.vm

import android.content.Intent
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.gmapexample1.GeoPositionActivity
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.IMapModel

class MapVM(var view: IActivity, var model: IMapModel) : IMapVM {
    override fun geo() {
        model.actvity.goPosition(false)
    }

    override fun onResume() {
        model.startButton.set(ButtonHandler({ geo() }, R.string.map_goPosition, view))
        model.stopButton.set(ButtonHandler({toMap()}, R.string.map_geo, view))
    }

    override fun toMap() {
            val mapIntent = model.actvity.position!!.newIntent //new Intent(Intent.ACTION_VIEW, geoUri);
            mapIntent.action = Intent.ACTION_VIEW
            mapIntent.setClass(view.context,GeoPositionActivity::class.java)
            view.activity.startActivity(mapIntent)
            view.activity.finish()
    }

    override fun onDestroy() {}
    override fun model(): IMapModel {
        return model
    }

}