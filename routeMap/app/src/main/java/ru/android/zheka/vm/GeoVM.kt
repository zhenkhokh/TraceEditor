package ru.android.zheka.vm

import android.content.Intent
import com.google.android.gms.maps.model.LatLng
import io.reactivex.functions.Consumer
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Point
import ru.android.zheka.db.UtilePointSerializer
import ru.android.zheka.gmapexample1.*
import ru.android.zheka.gmapexample1.R.*
import ru.android.zheka.gmapexample1.GeoPositionActivity.Companion.OFFLINE
import ru.android.zheka.model.IGeoModel

class GeoVM(var view: IActivity, var model: IGeoModel) : IGeoVM {
    var position: PositionInterceptor? = null
    var dialog: SingleChoiceDialog = MyDialog()
    var saveDialog = MySaveDialog()
    var monitor = Object()
    var ready = false
    var msg = ""

    override fun home() {
//TODO
    }

    override fun points() {
//TODO
    }

    override fun saveTrace() {
//TODO
    }

    override fun map() {
        if (position !!.state != PositionUtil.TRACE_PLOT_STATE.CENTER_END_COMMAND){
//            dialog.show (view.activity.getFragmentManager (), "Сообщение")
//            synchronized (monitor) {
//                println ("waiting dialog ...")
//                while (!ready) {
//                    try {
//                        monitor.wait ()
//                    } catch (e:InterruptedException){
//                        e.printStackTrace ()
//                    }
//                }
//                ready = false
//            }

            if (msg.contains ("yes")) {
                return
            }
            //else go to map
        }
        //if (//position.state!=null&&
        //		TraceActivity.isOtherMode(position.state))
        //	position.state = TRACE_PLOT_STATE.CENTER_START_COMMAND;
        if ((position !!.state != PositionUtil.TRACE_PLOT_STATE.CENTER_END_COMMAND && position !!.
        start == position !!.end || PositionUtil.LAT_LNG == position !!.end || position !!.
        end == null)
                    &&position !!.extraPoints.size > 0) //TODO move to getNewIntent
        position !!.end = UtilePointSerializer ().deserialize (position !!.extraPoints[position !!.
        extraPoints.size - 1])as LatLng
        if (position !!.end == null)
            if (position !!.centerPosition != null)
                position !!.end = position !!.centerPosition
            else
                position !!.end = position !!.start
        position !!.state = PositionUtil.TRACE_PLOT_STATE.CENTER_START_COMMAND
        var intent = position!!.newIntent
        //if (!position.extraPoints.isEmpty()){
        //	intent.putStringArrayListExtra(PositionUtil.EXTRA_POINTS, position.extraPoints);
        intent.setClass (view.context, MapsActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        if (MapsActivity.isOffline) intent.putExtra (PositionUtil.TITLE, OFFLINE)
        view.activity.startActivity (intent)
        view.activity.finish ()
    }

    override fun addWayPoints() {
//TODO
    }

    override fun onResume() {
        position = PositionInterceptor(view.activity)
        model.startButton.set(getButton(Consumer { a: Boolean? -> map() }, string.geo_maps))//TODO
    }

    override fun onDestroy() {}
    override fun model(): IGeoModel {
        return model
    }
    class MyDialog : SingleChoiceDialog("Маршрут не закончен. Хотите закончить?"
            , R.string.cancel_plot_trace
            , R.string.ok_plot_trace) {
        override fun positiveProcess() {
            synchronized(GeoPositionActivity.monitor) {
                GeoPositionActivity.msg = "yes"
                GeoPositionActivity.ready = true
                GeoPositionActivity.monitor.notify()
            }
        }

        override fun negativeProcess() {
            synchronized(GeoPositionActivity.monitor) {
                GeoPositionActivity.msg = "no"
                GeoPositionActivity.ready = true
                GeoPositionActivity.monitor.notify()
            }
        }
    }

    class MySaveDialog : SaveDialog() {
        var position: PositionInterceptor? = null
        override fun positiveProcess() {
            println("start positiveProcess")
            val point = Point()
            point.data = position!!.centerPosition
            point.name = nameField!!.text.toString()
            val dialog = AlertDialog("")
            if (point.name.isEmpty()) {
                //Toast.makeText(GeoPositionActivity.this, "text must not be empty", 15);
                dialog.msg = "Отсутсвует текст, введите название"
                dialog.show(fragmentManager, "Ошибка")
                return
            }
            if (DbFunctions.getPointByName(point.name) != null) {
                dialog.msg = "Точка с таким именем существует"
                dialog.show(fragmentManager, "Ошибка")
                return
            }
            println("start adding point $point")
            try {
                DbFunctions.add(point)
            } catch (e: java.lang.InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            println("end positiveProcess")
        }

        override fun newInstance(): SaveDialog {
            return this
        }
    }

    private fun getButton(consumer: Consumer<Boolean>, nameId: Int): ButtonHandler {
        return ButtonHandler(consumer
                , nameId
                , view)
    }
}