package ru.android.zheka.vm

import android.content.Intent
import android.view.View
import com.google.android.gms.maps.GoogleMap
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.fragment.HideMap
import ru.android.zheka.gmapexample1.*
import ru.android.zheka.model.IMapModel

class MapVM(var view: IActivity, var model: IMapModel) : IMapVM {
    override fun geo() {
        model.actvity.goPosition(false)
    }

    override fun onResume() {
        model.stopButton.set(ButtonHandler({ geo() }, R.string.map_goPosition, view))
        model.stopButton1.set(ButtonHandler({ toMap() }, R.string.map_geo, view))
        model.nextButton.set(ButtonHandler({ fakeStart() }, R.string.map_fakeStart, view))
        model.startButton.set(ButtonHandler({ home() }, R.string.map_home, view))
        model.startButton1.set(ButtonHandler({ hide() }, R.string.hide_panel_open, view))
        model.nextButton1.set(ButtonHandler({ mapType() }, R.string.map_mapType, view))
        model.inputVisible().set(View.GONE) // fit size
    }

    override fun hide() {
        view.switchToFragment(R.id.mapFragment, HideMap())
    }

    override fun home() {
        val intent = model.actvity.position?.newIntent
        intent?.setClass(view.context, MainActivity::class.java)
        intent?.action = Intent.ACTION_VIEW
        view.activity.startActivity(intent)
        view.activity.finish()
    }

    override fun mapType() {
        when (model.actvity.mapType.type) {
            MapTypeHandler.Type.NORMAL -> {
                MapTypeHandler.userCode = GoogleMap.MAP_TYPE_SATELLITE
                model.action().set("Изменена на спутниковую, поверните экран")
            }
            MapTypeHandler.Type.SATELLITE -> {
                MapTypeHandler.userCode = GoogleMap.MAP_TYPE_TERRAIN
                model.action().set("Изменена на рельефную, повторите просмотр")
            }
            MapTypeHandler.Type.TERRAIN -> {
                MapTypeHandler.userCode = GoogleMap.MAP_TYPE_HYBRID
                model.action().set("Изменена на гибридную, поверните экран")
            }
            MapTypeHandler.Type.HYBRID -> {
                MapTypeHandler.userCode = GoogleMap.MAP_TYPE_NORMAL
                model.action().set("Изменена на обычную, повторите просмотр")
            }
            else -> {}
        }
        model.actvity.mapType = MapTypeHandler(MapTypeHandler.userCode)
        //mMap.setMapType (mapType.getCode ());// setting has no effect
        geo();//?
    }


    override fun fakeStart() {
        model.isFakeStart = if (model.isFakeStart) false else true
        if (model.isFakeStart) {
            model.action().set("Задан псевдо старт")
        } else {
            model.action().set("Дан старт из местоположения")
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
        mapIntent.setClass(view.context, GeoPositionActivity::class.java)
        view.activity.startActivity(mapIntent)
        view.activity.finish()
    }

    override fun onDestroy() {}

    override fun model(): IMapModel {
        return model
    }
}