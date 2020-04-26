package ru.android.zheka.gmapexample1

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.CheckBox
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.activeandroid.Model
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import roboguice.activity.RoboListActivity
import roboguice.inject.InjectView
import ru.android.zheka.coreUI.AbstractActivity
import ru.android.zheka.coreUI.AbstractListActivity
import ru.android.zheka.coreUI.ErrorControl
import ru.android.zheka.coreUI.IActivity
import ru.android.zheka.db.DbFunctions
import ru.android.zheka.gmapexample1.edit.EditModel
import ru.android.zheka.gmapexample1.edit.Editable
import ru.android.zheka.jsbridge.JsCallable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

open class EditActivity : AbstractActivity_<ViewDataBinding>(),HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>
    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector!!
    }

    var resViewId = R.layout.activity_points
    var url = "file:///android_asset/edit.html"
    var name1: String? = null
    var model: EditModel? = null
    var adapter: MyAdapter? = null

    class MySaveDialog : SaveDialog() {
        var p: Editable? = null
        var model: EditModel? = null
        var status: ConcurrentHashMap<String?, Boolean?>? = null
        var pName: String? = null
        var activity: EditActivity? = null
        override fun positiveProcess() {
            val newName = nameField!!.text.toString()
            if (DbFunctions.getModelByName(newName, model!!.classTable) != null) {
                val dialog = AlertDialog("Введеное имя уже существует, введите другое")
                dialog.show(fragmentManager, "Переименование")
                return
            }
            DbFunctions.delete(p!!.model)
            p!!.name = newName
            try {
                DbFunctions.add(p!!.model)
            } catch (e: java.lang.InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            }
            val pStatus = status!![pName]!!
            status!!.remove(pName)
            status!![newName] = pStatus
            activity!!.updateView()
        }

        override fun newInstance(): SaveDialog {
            return this
        }
    }

    //public EditActivity(EditModel model){
    //	this.model = model;
    //}
    //public EditActivity(){}
    public override fun onCreate(savedInstanceState: Bundle?) {
        println("start EditActivity.onCreate")
        super.onCreate(savedInstanceState)
        setContentView(resViewId)
        if (model == null) model = intent.extras.getParcelable<Parcelable>(EDIT_MODEL) as EditModel
        if (model!!.describeContents() == -1) {
            println("model is not specified, nameId: "
                    + model!!.nameId + " name1Id:"
                    + model!!.name1Id + " clsName:"
                    + model!!.clsName + " clsPkg:"
                    + model!!.clsPkg)
            return
        }
        name = resources.getString(model!!.nameId) //R.string.points_column_name
        name1 = resources.getString(model!!.name1Id)
        updateView()
        println("end EditActivity.onCreate")
    }

    private fun updateView() {
        val dataTmp: MutableList<MutableMap<String, *>?>? = ArrayList()
        var map: MutableMap<String, Any>
        val models = DbFunctions.getTablesByModel(model!!.classTable)
        if (models != null) {
            val iterator: Iterator<*> = models.iterator()
            while (iterator.hasNext()) {
                val model = iterator.next() as Model
                val editable = Editable(model)
                map = HashMap<String, Any>()
                map[name!!] = editable!!.name!!
                map[name1!!] = false
                val key = editable.name
                //keep true values
                if (status[key] == null || status[key] == false) status[key] = false
                println("read point:" + editable.model.toString())
                dataTmp?.add(map)
            }
        }
        adapter = MyAdapter(this
                , dataTmp
                , R.layout.row_edit
                , arrayOf(name, name1), intArrayOf(R.id.text1, R.id.check))
        listAdapter = adapter
    }

    /*@Override
		public void onListItemClick(ListView l, View v,int position, long id) {
			System.out.println("------ start EditActivity.onListItemClick");
			Map<String,Object> data = (Map)l.getAdapter().getItem(position);
			String pName = (String)l.getAdapter().getItem(position);
			CheckBox checkBox = (CheckBox)v.findViewById(R.id.check);
			boolean newStatus;
			if (checkBox.isChecked()){
				newStatus = false;
			}else{
				newStatus = true;
			}
			checkBox.setChecked(newStatus);
			status.put(pName, newStatus);
			System.out.println("------ end EditActivity.onListItemClick");
		}
		*/
     open fun nextView(`val`: String) {
        val intent = intent
        if (`val`.contentEquals(HOME)) {
            intent.setClass(context, MainActivity::class.java)
            intent.action = Intent.ACTION_VIEW
            startActivity(intent)
            finish()
        }
        if (`val`.contentEquals(REMOVE)) {
            var cnt = 0
            val iterator: Iterator<String?> = status.keys.iterator()
            while (iterator
                            .hasNext()) {
                val pName = iterator.next()
                val pStatus = status[pName]!!
                if (pStatus) {
                    val p = DbFunctions.getModelByName(pName, model!!.classTable)
                    DbFunctions.delete(p!!)
                    //android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
                    runOnUiThread { //status.remove(pName);
                        updateView()
                    }
                    cnt++
                }
            }
            /*if(cnt>0){
					  updateView();
    		          //onRestart();
					  this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
		    		          adapter.notifyDataSetChanged();
		    		          //findViewById(layout.activity_points)
		    		          getListView().refreshDrawableState();
		    		          //((TextView)getListView().findViewById(model.nameId)).refreshDrawableState();

						}
					});

				  }else*/if (cnt == 0) Toast.makeText(this, "Не выбрано ни одного элемента", 15).show() else {
                while (status.containsValue(true)) {
                    val iterator: Iterator<String?> = status.keys.iterator()
                    while (iterator
                                    .hasNext()) {
                        status.remove(iterator.next(), true)
                    }
                }
                //AlertDialog dialog = new AlertDialog("Удаление завершено");
                //dialog.show(getFragmentManager(), "Сообщение");
            }
        }
        if (`val`.contentEquals(RENAME)) {
            val cnt = 0
            val iterator: Iterator<String?> = status.keys.iterator()
            while (iterator
                            .hasNext()) {
                val pName = iterator.next()
                val pStatus = status[pName]!!
                if (pStatus) {
                    Toast.makeText(this, "Переименование $pName", 15).show()
                    val p = Editable(DbFunctions
                            .getModelByName(pName
                                    , model!!.classTable)!!)
                    val dialog = MySaveDialog().newInstance(pName) as MySaveDialog
                    dialog.activity = this
                    dialog.model = model
                    dialog.p = p
                    dialog.pName = pName
                    dialog.status = status
                    dialog.show(fragmentManager, "Переименование")
                }
            }
            /*if(cnt>0){
					  intent.putExtra("editModel", model);
			          intent.setClass(this.context, getClass());
			          intent.setAction(Intent.ACTION_VIEW);
			          startActivity(intent);
			          finish();

					  String PRM;
					  if (model.clsPkg.contentEquals("Trace"))
						  PRM = MainActivity.EDIT_TRACE;
					  else
						  PRM = MainActivity.EDIT_POINT;
					  MainActivity m =  new MainActivity();
					  m.setIntent(intent);
					  m.nextView(PRM);
				  }else*/if (cnt == 0) Toast.makeText(this, "Не выбрано ни одного элемента", 15).show()
        }
    }

//    override fun getVebWebView(): WebView {
//        return vebViewHome!!
//    }


    class MyAdapter(val activity: Activity, data: List<MutableMap<String, *>?>?,
                    resource: Int, from: Array<String?>?, to: IntArray?) : SimpleAdapter(activity, data, resource, from, to) {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view: View? = null
            println("------ start MyAdapter.getView")
            view = if (convertView == null) {
                val inflator = activity.layoutInflater
                inflator.inflate(R.layout.row_edit, null)
            } else {
                convertView
            }
            val checkBox = view!!.findViewById<View>(R.id.check) as CheckBox
            val text = view.findViewById<View>(R.id.text1) as TextView
            val data = super.getItem(position) as Map<String?, Any>
            val pName = data[name] as String?
            checkBox.setOnCheckedChangeListener { box, isChecked -> status[pName] = box.isChecked }
            val newStatus = status[pName]!!
            checkBox.isChecked = newStatus
            text.text = pName
            println("name:$pName newstatus:$newStatus")
            println("------ end MyAdapter.getView")
            return view
        }
    }
    companion object {
        var name: String? = null
        var status = ConcurrentHashMap<String?, Boolean?>()


        const val EDIT_MODEL = "editModel"
        const val RENAME = "rename"
        const val REMOVE = "remove"
        const val HOME = "home"
    }

    override val layoutId: Int = R.layout.activity_points


    override fun initComponent() {
        AndroidInjection.inject(this)
    }

    override fun getActivity(): Activity {
        return this
    }

    override fun onInitBinding(binding: ViewDataBinding?) {

    }

    override fun onResumeBinding(binding: ViewDataBinding?) {

    }

    override fun onDestroyBinding(binding: ViewDataBinding?) {

    }
}

abstract class AbstractActivity_ //RoboFragmentActivity
<B : ViewDataBinding> : AbstractListActivity(), IActivity {
    private var error: ErrorControl? = null
    var binding: B? = null
    protected abstract val layoutId: Int
    protected abstract fun initComponent()
    protected abstract fun onInitBinding(binding: B?)
    protected abstract fun onResumeBinding(binding: B?)
    protected abstract fun onDestroyBinding(binding: B?)
    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        supportActionBar!!.setIcon(R.mipmap.ic_launcher)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        error = ErrorControl(this)
        binding = DataBindingUtil.inflate<B>(LayoutInflater.from(context)!!, layoutId,null,false)
    }

    override fun onStart() {
        initComponent()
        super.onStart()
        onInitBinding(binding)
    }

    override fun onResume() {
        super.onResume()
        onResumeBinding(binding)
    }

    override fun onDestroy() {
        super.onDestroy()
        onDestroyBinding(binding)
        binding!!.unbind()
    }

    override fun showError(throwable: Throwable) {
        error!!.showError(throwable) { a: Boolean? -> }
    }

    override fun getContext(): Context {
        return this
    }

    override fun switchToFragment(fragmentId: Int, fragment: Fragment) {
        val transaction = manager.beginTransaction()
        transaction.replace(fragmentId, fragment)
        transaction.commit()
    }

    override fun removeFragment(fragment: Fragment) {
        val transaction = manager.beginTransaction()
        transaction.remove(fragment)
        transaction.commit()
    }

    override fun getManager(): FragmentManager {
        return supportFragmentManager
    }
}