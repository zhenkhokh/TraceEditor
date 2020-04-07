package ru.android.zheka.vm

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Message
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Single
import io.reactivex.SingleTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.coreUI.RxTransformer
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Point
import ru.android.zheka.db.UtilePointSerializer
import ru.android.zheka.gmapexample1.*
import ru.android.zheka.gmapexample1.R.*
import ru.android.zheka.gmapexample1.GeoPositionActivity.Companion.OFFLINE
import ru.android.zheka.model.IGeoModel

class GeoVM(var view: IActivity, var model: IGeoModel) : IGeoVM {

    //    var dialog: MyDialog = MyDialog()
//    var saveDialog = MySaveDialog()
    var monitor = Object()
    var ready = false
    var msg = ""

    init {
        GeoVM.view = view
    }

    override fun home() {
//TODO
    }

    override fun points() {
//TODO
    }

    override fun savePoint() {
        GeoSaveDialog.newInstance(string.hint_dialog_point)
                .show(getFragmentManager(), "dialog")
        //dialog.show(getSupportFragmentManager(), "dialog");
    }

    override fun map() {
        if (position!!.state != PositionUtil.TRACE_PLOT_STATE.CENTER_END_COMMAND) {
//            dialog.setOnCancelListener({ a-> goToMap()})
//            dialog.
            Single.just(true)
                    .doOnSubscribe({ a -> GeoVM.show(view.activity.getFragmentManager(), "Сообщение") })
                    .compose(RxTransformer.singleIoToMain())
                    .subscribe({ a -> println("Finish") }, { e -> println("Error:" + e.message) })

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


            //else go to map
        }
    }


    override fun addWayPoints() {
//TODO
    }

    override fun onResume() {
        position = PositionInterceptor(view.activity)
        model.startButton.set(getButton(Consumer { a: Boolean? -> map() }, string.geo_maps))//TODO
        model.stopButton.set(getButton(Consumer { a: Boolean? -> savePoint() }, string.geo_save_point))//TODO
    }

    override fun onDestroy() {}
    override fun model(): IGeoModel {
        return model
    }

    companion object : SingleChoiceDialog("Маршрут не закончен. Хотите закончить?"
            , string.cancel_plot_trace
            , string.ok_plot_trace) {
        override fun positiveProcess() {
        }

        override fun negativeProcess() {
            goToMap()
        }

        var position: PositionInterceptor? = null
        lateinit var view: IActivity
        fun goToMap() {
            //if (//position.state!=null&&
            //		TraceActivity.isOtherMode(position.state))
            //	position.state = TRACE_PLOT_STATE.CENTER_START_COMMAND;
            if ((position!!.state != PositionUtil.TRACE_PLOT_STATE.CENTER_END_COMMAND && position!!.start == position!!.end || PositionUtil.LAT_LNG == position!!.end || position!!.end == null)
                    && position!!.extraPoints.size > 0) //TODO move to getNewIntent
                position!!.end = UtilePointSerializer().deserialize(position!!.extraPoints[position!!.extraPoints.size - 1]) as LatLng
            if (position!!.end == null)
                if (position!!.centerPosition != null)
                    position!!.end = position!!.centerPosition
                else
                    position!!.end = position!!.start
            position!!.state = PositionUtil.TRACE_PLOT_STATE.CENTER_START_COMMAND
            var intent = position!!.newIntent
            //if (!position.extraPoints.isEmpty()){
            //	intent.putStringArrayListExtra(PositionUtil.EXTRA_POINTS, position.extraPoints);
            intent.setClass(view.context, MapsActivity::class.java)
            intent.action = Intent.ACTION_VIEW
            if (MapsActivity.isOffline) intent.putExtra(PositionUtil.TITLE, OFFLINE)
            view.activity.startActivity(intent)
            view.activity.finish()
        }

        class GeoSaveDialog {
            companion object : SaveDialog() {
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
        }
    }

    private fun getButton(consumer: Consumer<Boolean>, nameId: Int): ButtonHandler {
        return ButtonHandler(consumer
                , nameId
                , view)
    }
}