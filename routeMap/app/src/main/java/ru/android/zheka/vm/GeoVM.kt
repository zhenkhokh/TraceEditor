package ru.android.zheka.vm

import android.content.Intent
import io.reactivex.Single
import io.reactivex.functions.Consumer
import ru.android.zheka.coreUI.*
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Point
import ru.android.zheka.db.UtilePointSerializer
import ru.android.zheka.gmapexample1.*
import ru.android.zheka.gmapexample1.GeoPositionActivity.Companion.OFFLINE
import ru.android.zheka.gmapexample1.R.string
import ru.android.zheka.model.IGeoModel
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.trace.TraceEndVM
import ru.android.zheka.vm.trace.TraceStartVM
import ru.android.zheka.vm.trace.TraceWayPointsVM

class GeoVM(var view: IActivity, var model: IGeoModel) : IGeoVM {

    lateinit var spinnerOption: String

    override fun home() {
        val intent = model.position!!.updatePosition()
        intent.setClass(view.context, MainActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        view.activity.startActivity(intent)
        view.activity.finish()
    }

//    override fun points() {
//        if ( //position.state!=null&&
//                TraceActivity.isOtherMode(position!!.state))
//            position!!.state = PositionUtil.TRACE_PLOT_STATE.CENTER_COMMAND
//        val intent = position!!.newIntent
//        intent.setClass(view.context, TraceActivity::class.java)
//        intent.action = Intent.ACTION_VIEW
//        view.activity.startActivity(intent)
//        view.activity.finish()
//    }

    override fun savePoint() {
        val dialog = GeoSaveDialog()
        dialog.model = model
        dialog.newInstance(string.hint_dialog_point)
                .show(view.activity.getFragmentManager(), "dialog")
    }

//    override fun pointToTrace() {
//        val intent = position!!.updatePosition()
//        intent.setClass(view.context, TraceActivity::class.java)
//        view.activity.startActivity(intent)
//        view.activity.finish()
//    }

    override fun map() {
        if (model.position!!.state != PositionUtil.TRACE_PLOT_STATE.END_COMMAND) {
            Single.just(true)
                    .doOnSubscribe({
                        val dialog = ToMapDialog()
                        dialog.vm = this
                        dialog.show(view.activity.getFragmentManager(), "Сообщение") })
                    .compose(RxTransformer.singleIoToMain())
                    .subscribe({ a -> println("Finish successfully") }, { e -> println("Error:" + e.message) })
        }
    }

    override fun addCPoint() {
        val options = getOptions()
        if (spinnerOption.equals(options[0])) {
            startCp()
            return
        }
        if (spinnerOption.equals(options[1])) {
            wayCp()
            return
        }
        if (spinnerOption.equals(options[2])) {
            endCp();
        }
    }

    private fun endCp() {
        val vm = TraceEndVM(view, LatLngModel(view.context))
        vm.panelModel = model
        vm.finish(model.point)
    }

    private fun wayCp() {
        val vm = TraceWayPointsVM(view, llModel(), model)
        vm.add()
    }

    lateinit var llModel_: LatLngModel

    private fun llModel(): LatLngModel {
        if (!this::llModel_.isInitialized) {
            llModel_ = LatLngModel(view.context)
        }
        val point = Point()
        point.name = UtilePointSerializer().serialize(model.point) as String
        point.data = model.point
        llModel_._customPoints.add(point)
        llModel_.checked.add(true)
        return llModel_
    }

    private fun startCp() {
        val vm = TraceStartVM(view, LatLngModel(view.context))
        vm.panelModel = model
        vm.resetAndStartTrace(model.position, model.point)
    }


//    override fun addWayPoints() {
//        val model = EditModel()
//        model.clsName = "Point"
//        model.clsPkg = "ru.android.zheka.db"
//        model.name1Id = R.string.points_column_name1
//        model.nameId = R.string.points_column_name
//        val intent = position!!.updatePosition()
//        intent.putExtra(EditActivity.EDIT_MODEL, model)
//        intent.action = Intent.ACTION_VIEW
//        intent.setClass(view.context, WayPointsToTrace::class.java)
//        view.activity.startActivity(intent)
//        view.activity.finish()
//    }

    override fun onDestroy() {}

    override fun model(): IGeoModel {
        return model
    }

    fun goToMap() {
//            if ((position!!.state != PositionUtil.TRACE_PLOT_STATE.END_COMMAND && position!!.start == position!!.end || PositionUtil.LAT_LNG == position!!.end || position!!.end == null)
//                    && position!!.extraPoints.size > 0) //TODO move to getNewIntent
//                position!!.end = UtilePointSerializer().deserialize(position!!.extraPoints[position!!.extraPoints.size - 1]) as LatLng
//            if (position!!.end == null) if (position!!.centerPosition != null) position!!.end = position!!.centerPosition else position!!.end = position!!.start
//            position!!.state = PositionUtil.TRACE_PLOT_STATE.CENTER_START_COMMAND
//            position!!.start = position!!.location

        val intent = model.position!!.newIntent
//            val intent = view.activity.intent
        intent.setClass(view.context, MapsActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        if (MapsActivity.isOffline) intent.putExtra(PositionUtil.TITLE, OFFLINE)
        view.activity.startActivity(intent)
        view.activity.finish()
    }

    class ToMapDialog: SingleChoiceDialog("Маршрут не закончен. Хотите закончить?"
            , string.cancel_plot_trace
            , string.ok_plot_trace) {


        lateinit var vm: GeoVM

        override fun positiveProcess() {
        }

        override fun negativeProcess() {
            vm.goToMap()
        }



    }

    class GeoSaveDialog : SaveDialog() {
        lateinit var model: IGeoModel
            override fun positiveProcess() {
                    println("start positiveProcess")
                    val point = Point()
                    point.data = model.position!!.centerPosition
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

    override fun onResume() {
        model.startButton.set(getButton(Consumer { a: Boolean? -> home() }, string.geo_home))
        if (isManualOnly())
            model.startButton1.set(getButton(Consumer { goPosition() }, string.map_goPosition))
        model.nextButton.set(getButton(Consumer { savePoint() }, string.geo_save_point))
        model.stopButton1.set(getButton(Consumer { addCPoint() }, string.geo_point_to_trace))
        model.stopButton.set(getButton(Consumer {  map() }, string.geo_maps))
        model.inputVisible().set(IPanelModel.COMBO_BOX_VISIBLE)
        model.action().set(view.activity.resources.getString(string.action_geo))
        model.spinner.set(SpinnerHandler({ spinnerOption = it }, {}, getOptions(), view))
    }

    private fun isManualOnly(): Boolean {
        val manual = view.activity.getString(string.timerdata1)
        return model.config.tenMSTime.equals(manual)
    }

    override fun goPosition() {
        val intent = model.position.updatePosition()
        TimerService.mListners?.first?.onReceive(view.context, intent)
    }

    private fun getOptions(): List<String> {
        val res = view.activity.resources
        return listOf(res.getString(string.geo_spinner_start),
                res.getString(string.geo_spinner_way),
                res.getString(string.geo_spinner_end))
    }
}
