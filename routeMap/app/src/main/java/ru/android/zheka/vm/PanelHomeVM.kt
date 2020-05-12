package ru.android.zheka.vm

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import io.reactivex.functions.Consumer
import ru.android.zheka.coreUI.ButtonHandler
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.coreUI.IPanelModel
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.db.Point
import ru.android.zheka.fragment.*
import ru.android.zheka.gmapexample1.EditActivity
import ru.android.zheka.gmapexample1.GeoPositionActivity
import ru.android.zheka.gmapexample1.PositionInterceptor
import ru.android.zheka.gmapexample1.R.*
import ru.android.zheka.gmapexample1.edit.EditModel
import ru.android.zheka.model.HomeModel
import ru.android.zheka.model.IHomeModel
import java.util.*

class PanelHomeVM(val view: IActivity, val model: IPanelModel,
                  val edit: IEdit, val editTraces: IEditTraces, val trace: ITrace, val enterPoint:IEnterPoint) : IPanelHomeVM {
    var fragment: Fragment

    init {
        fragment = view.manager.findFragmentById(id.mainFragment)!!
    }


    override fun settings() {
        view.removeFragment(fragment)
        fragment = Settings()
        view.switchToFragment(id.mainFragment, fragment)
    }

    override fun info() {
        val activity = view.activity
        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(layout.scrolable_dialog, null)
        val tv = view.findViewById<TextView>(id.textmsg) //new TextView (this);
        val `is` = this.view.activity.resources.openRawResource(raw.info)
        val scanner = Scanner(`is`)
        scanner.useDelimiter("\n")
        val sb = StringBuilder()
        while (scanner.hasNextLine()) sb.append(scanner.nextLine())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv.text = Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_LEGACY)
        } else tv.text = Html.fromHtml(sb.toString())
        AlertDialog.Builder(activity)
                .setView(view)
                .setTitle(activity.resources.getString(string.home_info_btn))
                .setCancelable(true)
                .create()
                .show()
    }

    override fun enterPoint() {
        view.removeFragment(fragment)
        fragment = enterPoint as EnterPoint
        view.switchToFragment(id.latLngFragment, fragment)
//        val intent = view.activity.intent
//        intent.action = Intent.ACTION_VIEW
//        intent.setClass(view.activity, AddressActivity::class.java)
//        view.activity.startActivity(intent)
//        view.activity.finish()
    }

    override fun geo() {
        val position = PositionInterceptor(view.activity)
        val intent = position.updatePosition()
        intent.setClass(view.context, GeoPositionActivity::class.java)
        intent.action = Intent.ACTION_VIEW
        //explicit activity
        view.activity.startActivity(intent)
        view.activity.finish()
    }

    override fun editPoints() {
//        editItem("Point", string.points_column_name, string.points_column_name1)
        view.removeFragment(fragment)
        fragment = edit as Edit
        view.switchToFragment(id.latLngFragment, fragment)
    }

    override fun editTraces() {
//        editItem("Trace", string.traces_column_name, string.traces_column_name1)
        view.removeFragment(fragment)
        fragment = editTraces as EditTraces
        view.switchToFragment(id.latLngFragment, fragment)
    }

    fun editItem(item: String?, nameId: Int, name1Id: Int) {
        val intent = view.activity.intent
        val model = EditModel()
        model.clsName = item
        model.clsPkg = "ru.android.zheka.db"
        model.name1Id = name1Id
        model.nameId = nameId
        intent.putExtra(EditActivity.EDIT_MODEL, model)
        intent.action = Intent.ACTION_VIEW
        intent.setClass(view.context, EditActivity::class.java)
        view.activity.startActivity(intent)
        view.activity.finish()
    }

    override fun pointNavigate() {
        view.removeFragment(fragment)
        fragment = LatLng()
        view.switchToFragment(id.latLngFragment, fragment)
    }

    override fun createTrace() {
        view.removeFragment(fragment)
        fragment = trace as Trace
        view.switchToFragment(id.latLngFragment, fragment)
//        val intent = view.activity.intent
//        intent.setClass(view.activity, PointToTraceActivity::class.java)
//        intent.action = Intent.ACTION_VIEW
//        view.activity.startActivity(intent)
//        view.activity.finish()
    }

    private fun getButton(consumer: Consumer<Boolean>, nameId: Int): ButtonHandler {
        return ButtonHandler(consumer
                , nameId
                , view)
    }

    override fun onResume() {
        model.inputVisible().set(View.GONE)
        model.startButton.set(getButton(Consumer { settings() }, string.home_settings_btn))
        model.stopButton.set(getButton(Consumer { enterPoint() }, string.home_address_btn))
        if (isPoints()) {
            model.nextButton.set(getButton(Consumer { pointNavigate() }, string.home_points_btn))
            model.stopButton1.set(getButton(Consumer { editPoints() }, string.home_editPoint_btn))
            model.nextButton1.set(getButton(Consumer { createTrace() }, string.home_toTrace_btn))
        }
        model.startButton1.set(getButton(Consumer { editTraces() }, string.home_editTrace_btn))
        model.startButton2.set(getButton(Consumer { geo() }, string.home_geo_btn))
        model.stopButton2.set(getButton(Consumer { info() }, string.home_info_btn))
//TODO        model.stopButton2.set(getButton(Consumer { a: Boolean? -> hide() }, string.home_hide_btn))
    }

    private fun isPoints(): Boolean {
        val list = DbFunctions.getTablesByModel(Point::class.java)
        return list != null && list.isNotEmpty()
    }

    private fun hide() {

    }

    override fun onDestroy() {}
    override fun model(): IHomeModel {
        return model as HomeModel
    }

}