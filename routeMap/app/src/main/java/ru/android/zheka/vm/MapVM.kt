package ru.android.zheka.vm

import android.content.Intent
import android.widget.Toast
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.gmapexample1.GeoPositionActivity
import ru.android.zheka.gmapexample1.MainActivity
import ru.android.zheka.gmapexample1.MapsActivity
import ru.android.zheka.gmapexample1.R
import ru.android.zheka.model.IMapModel

class MapVM(var view: IActivity, var model: IMapModel) : IMapVM {
    override fun geo() {
        model.actvity.goPosition(false)
    }

    override fun onResume() {
        model.startButton.set(ButtonHandler({ geo() }, R.string.map_goPosition, view))
        model.stopButton.set(ButtonHandler({toMap()}, R.string.map_geo, view))
        model.nextButton.set(ButtonHandler({fakeStart()}, R.string.map_fakeStart, view))
        model.startButton1.set(ButtonHandler({ home() }, R.string.map_home, view))
    }

    override fun home() {
        val intent = model.actvity.position?.newIntent
        intent?.setClass(view.context, MainActivity::class.java)
        intent?.action = Intent.ACTION_VIEW
        view.activity.startActivity(intent)
        view.activity.finish()
    }

    override fun fakeStart() {
        model.isFakeStart = if (model.isFakeStart) false else true
        if (model.isFakeStart) {
            Toast.makeText(view.context, "Задан псевдо старт", 15).show()
        } else {
            Toast.makeText(view.context, "Дан старт из местоположения", 15).show()
        }
        val intent = view.activity.intent
        intent.setClass(view.context, MapsActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        view.activity.startActivity(intent)
        view.activity.finish()
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