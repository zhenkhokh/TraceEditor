package ru.android.zheka.gmapexample1

import android.content.Intent
import android.widget.Toast
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Point
import ru.android.zheka.db.UtilePointSerializer
import ru.android.zheka.gmapexample1.PositionUtil.TRACE_PLOT_STATE
import java.util.*

//import java.util.concurrent.ConcurrentHashMap.KeySetView;
class WayPointsToTrace : EditActivity() {
    override fun nextView(`val`: String) {
        if (`val`.contains(EditActivity.Companion.HOME)) {
            super.nextView(`val`)
        }
        var intent = intent
        if (`val`.contains(ADD_WAYPOINTS)) {
            //TODO go to GeoPosition
            val position = PositionInterceptor(this)
            intent = try {
                position.positioning()
            } catch (e: Exception) {
                Toast.makeText(this, "Невозможно выполнить: начало маршрута не задано", 15).show()
                e.printStackTrace()
                return
            }
            if (TraceActivity.isOtherMode(position.state)) {
                val dialog = AlertDialog("Подан другой режим, для сброса вернитесь в начало маршрута")
                dialog.show(fragmentManager, "Ошибка")
                return
            }
            if (position.state == TRACE_PLOT_STATE.CENTER_END_COMMAND) {
                Toast.makeText(this, "Маршрут задан, перейдите к просмотру", 15).show()
                return
            }
            if (position.state == TRACE_PLOT_STATE.CENTER_COMMAND) {
                Toast.makeText(this, "Невозможно выполнить: начало маршрута не задано", 15).show()
                return
            }
            if (position.state == TRACE_PLOT_STATE.CENTER_CONNECT_COMMAND) {
                //KeySetView<String, Boolean> names = status.keySet(true);//since 1.8
                val names: MutableSet<String> = HashSet()
                run {
                    val iterator: Iterator<*> = status.keys.iterator()
                    while (iterator.hasNext()) {
                        val key = iterator.next() as String
                        if (status[key]!!) names.add(key)
                    }
                }
                if (names.isEmpty()) {
                    Toast.makeText(this, "Выберете одну или несколько точек", 15).show()
                    return
                }
                Toast.makeText(this, "Путевые точки успешно добавлены. Добавьте конец маршрута", 15).show()
                val util = UtilePointSerializer()
                val tmp = ArrayList<String?>()
                val iterator: Iterator<*> = names.iterator()
                while (iterator.hasNext()) {
                    val name = iterator.next() as String
                    val point = DbFunctions.getModelByName(name, Point::class.java) as Point
                    position.centerPosition = point.data
                    tmp.add(util.serialize(point.data) as String)
                }
                position.setExtraPointsFromCopy(tmp)
                intent = position.newIntent
                intent.setClass(context, GeoPositionActivity::class.java)
                intent.action = Intent.ACTION_VIEW
                startActivity(intent)
                finish()
            } else Toast.makeText(this, "Этот случай недостижим.", 15).show()
        }
    }

    companion object {
        const val ADD_WAYPOINTS = "addWaypoints"
    }

    init {
        resViewId = R.layout.activity_points
        url = "file:///android_asset/waypoints.html"
    }
}