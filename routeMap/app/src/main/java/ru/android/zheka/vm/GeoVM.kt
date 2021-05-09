package ru.android.zheka.vm

import android.content.Intent
import android.view.View
import io.reactivex.Single
import io.reactivex.functions.Consumer
import ru.android.zheka.coreUI.*
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Point
import ru.android.zheka.db.UtilePointSerializer
import ru.android.zheka.fragment.HideGeo
import ru.android.zheka.gmapexample1.*
import ru.android.zheka.gmapexample1.GeoPositionActivity.Companion.OFFLINE
import ru.android.zheka.gmapexample1.R.string
import ru.android.zheka.model.IGeoModel
import ru.android.zheka.model.LatLngModel
import ru.android.zheka.vm.trace.TraceEndVM
import ru.android.zheka.vm.trace.TraceStartVM
import ru.android.zheka.vm.trace.TraceWayPointsVM

class GeoVM(var view: IActivity, var model: IGeoModel) : IGeoVM {

    companion object {
        lateinit var model: IGeoModel
    }

    lateinit var spinnerOption: String

    override fun home() {
        val intent = model.position.newIntent
        intent.setClass(view.activity, MainActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        view.activity.startActivity(intent)
        view.activity.finish()
    }

    override fun savePoint() {
        val dialog = GeoSaveDialog()
        dialog.model = model
        dialog.newInstance(string.hint_dialog_point)
                .show(view.activity.getFragmentManager(), "dialog")
    }

    override fun map() {
        if (model.position!!.state != PositionUtil.TRACE_PLOT_STATE.END_COMMAND) {
            Single.just(true)
                    .doOnSubscribe({
                        val dialog = ToMapDialog()
                        dialog.vm = this
                        dialog.show(view.activity.getFragmentManager(), "Сообщение")
                    })
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
        val vm = TraceEndVM(view, LatLngModel(view.activity))
        vm.panelModel = model
        vm.finish(model.point)
//        vm.finish(model.point)
//        vm.finish(model.point)
        llModel_ = null
        model.stopButton.get()?.visible?.set(View.GONE)
    }

    private fun wayCp() {
        val vm = TraceWayPointsVM(view, llModel(), model)
        vm.add()
    }

    var llModel_: LatLngModel? = null

    private fun llModel(): LatLngModel {
        llModel_ = llModel_ ?: LatLngModel(view.activity)
        val point = Point()
        point.name = UtilePointSerializer().serialize(model.point) as String
        point.data = model.point
        llModel_?.custom = true
        llModel_?._customPoints?.add(point)
        llModel_?.checked?.add(true)
        return llModel_!!
    }

    private fun startCp() {
        val vm = TraceStartVM(view, LatLngModel(view.activity))
        vm.panelModel = model
//        vm.resetTrace()
        vm.resetAndStartTrace(model.position, model.point)
//        state = vm.resetAndStartTrace(model.position, model.point)
//        state = vm.resetAndStartTrace(model.position, model.point)
    }

    override fun onDestroy() {
        model.hidePanel.set(View.VISIBLE)
        model.stopButton1.get()?.visible?.set(View.GONE)// add point
        model.startButton2.get()?.visible?.set(View.GONE)// show
    }

    override fun model(): IGeoModel {
        return model
    }

    fun goToMap() {
        val intent = model.position!!.newIntent
        intent.setClass(view.activity, MapsActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        if (MapsActivity.isOffline) intent.putExtra(PositionUtil.TITLE, OFFLINE)
        view.activity.startActivity(intent)
        view.activity.finish()
    }

    class ToMapDialog : SingleChoiceDialog("Маршрут не закончен. Хотите закончить?"
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
            model.stopButton.set(getButton(Consumer { goPosition() }, string.map_goPosition))
        else
            model.startButton1.get()?.visible?.set(View.INVISIBLE)
        model.nextButton.set(getButton(Consumer { savePoint() }, string.geo_save_point))
        val isMaiToMap = isMainToMap()
        val isOnline = isOnline()
        if (isMaiToMap && isOnline) {
            model.startButton2.set(getButton(Consumer { map() }, string.geo_maps))
        }
        if (isMaiToMap) {
            model.nextButton2.get()?.visible?.set(View.GONE)
        }
        if (isOnline) {
            model.stopButton1.set(getButton(Consumer { addCPoint() }, string.geo_point_to_trace))
            model.inputVisible().set(IPanelModel.COMBO_BOX_VISIBLE)
            model.action().set(view.activity.resources.getString(string.action_geo))
            model.spinner.set(SpinnerHandler(Consumer{ spinnerOption = it }, Consumer{}, getOptions(), view))
        } else {
            model.action().set(view.activity.resources.getString(string.action_geo_offline))
            model.inputVisible().set(View.GONE) // fit size
        }
        model.startButton1.set(getButton(Consumer { hide() }, string.hide_panel_open))
        model.hidePanel.set(View.GONE)
    }

    private fun isOnline(): Boolean {
        return !model.config.offline!!.toBoolean()
    }

    override fun hide() {
        view.switchToFragment(R.id.geoFragment, HideGeo())
    }

    private fun isMainToMap(): Boolean {
        return llModel_?.nextButton2?.get()?.visible?.get()?.equals(View.GONE) ?: true
    }

    private fun isManualOnly(): Boolean {
        val manual = view.activity.getString(string.timerdata1)
        return model.config.tenMSTime.equals(manual)
    }

    override fun goPosition() {
        val intent = model.position.updatePosition()
        TimerService.mListners?.first?.onReceive(view.context(), intent)
    }

    private fun getOptions(): List<String> {
        val res = view.activity.resources
        return listOf(res.getString(string.geo_spinner_start),
                res.getString(string.geo_spinner_way),
                res.getString(string.geo_spinner_end))
    }
}
