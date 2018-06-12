package ru.android.zheka.gmapexample1;

	import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

	import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectView;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.db.Point;
import ru.android.zheka.db.UtilePointSerializer;
import ru.android.zheka.gmapexample1.R;
import ru.android.zheka.gmapexample1.PositionUtil.TRACE_PLOT_STATE;
import ru.android.zheka.jsbridge.JsCallable;

	import com.activeandroid.Model;

	import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

	public class EditActivity extends RoboListActivity implements JsCallable{
			public static final String EDIT_MODEL = "editModel";
			public static final String RENAME = "rename";
			public static final String REMOVE = "remove";
			public static final String HOME = "home";
			@InjectView(R.id.webViewPoint)
			WebView vebViewHome;
			int resViewId = R.layout.activity_points;
			String url = "file:///android_asset/edit.html";
			Context context = this;
	        String name,name1;
	        ConcurrentHashMap<String,Boolean> status = new ConcurrentHashMap<String, Boolean>();
	        EditModel model=null;
	        MyAdapter adapter;
	        static public class MySaveDialog extends SaveDialog{
	        	public Editable p;
	        	public EditModel model;
	        	public ConcurrentHashMap<String,Boolean> status;
	        	public String pName;
	        	public EditActivity activity;

				@Override
				protected void positiveProcess() {
					String newName = nameField.getText().toString();
					if (DbFunctions.getModelByName(newName, model.getClassTable())!=null){
						AlertDialog dialog = new AlertDialog("Введеное имя уже существует, введите другое");
						dialog.show(getFragmentManager(), "Переименование");
						return;
					}
					DbFunctions.delete(p.model);
					p.setName(newName);
					try{DbFunctions.add(p.model);
					}catch(java.lang.InstantiationException e){
						e.printStackTrace();
					}catch(IllegalAccessException e){
						e.printStackTrace();
					}catch(IllegalArgumentException e){
						e.printStackTrace();
					}
					boolean pStatus = status.get(pName);
					status.remove(pName);
					status.put(newName, pStatus);
					activity.updateView();
				}
				@Override
				protected SaveDialog newInstance() {
					return this;
				}
			}
	        //public EditActivity(EditModel model){
	        //	this.model = model;
	        //}
	        //public EditActivity(){}
		@Override
		public void onCreate(Bundle savedInstanceState) {
			System.out.println("start EditActivity.onCreate");
		        super.onCreate(savedInstanceState);
                setContentView(resViewId);
                if(model==null)
                	model = (EditModel)getIntent().getExtras().getParcelable(EDIT_MODEL);
                if (model.describeContents()==-1){
                	System.out.println("model is not specified, nameId: "
                			+model.nameId+" name1Id:"
                			+model.name1Id+" clsName:"
                			+model.clsName+" clsPkg:"
                			+model.clsPkg);
                	return;
                }                	
		        name = getResources().getString(model.nameId);//R.string.points_column_name
		        name1 = getResources().getString(model.name1Id);
		        updateView();
		 		MenuHandler m = new MenuHandler();
				m.initJsBridge(this,url);
				System.out.println("end EditActivity.onCreate");
		    }
		private void updateView() {
	         List<Map<String,Object>> dataTmp = new ArrayList<Map<String,Object>>();
	         Map map = new HashMap<String, Object>(); 
	         List<Model> models = DbFunctions.getTablesByModel(model.getClassTable());
	         if (models!=null)
		         for (Iterator iterator = models.iterator(); iterator.hasNext();) {
					Model model = (Model) iterator.next();
					Editable editable = new Editable(model);
					map = new HashMap();
					map.put(name, editable.getName());
					map.put(name1, false);
					String key = editable.getName();
					//keep true values
					if (status.get(key)==null||status.get(key)==false)
						status.put(key,false);
					System.out.println("read point:"+editable.model.toString());
					dataTmp.add(map);
		         }
	         adapter = new MyAdapter(this.context
	        		 ,dataTmp
	        		 ,R.layout.row_edit
	         		 ,new String[]{name,name1}
	         		 ,new int[]{R.id.text1,R.id.check});
	        setListAdapter(adapter);
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
		@Override
		public void nextView(String val) {
			Intent intent = getIntent();
			if (val.contentEquals(HOME)){
		          intent.setClass(this.context, MainActivity.class);
		          intent.setAction(Intent.ACTION_VIEW);
		          startActivity(intent);
		          finish();
			}
			if (val.contentEquals(REMOVE)){
				int cnt=0;
				for (Iterator<String> iterator = status.keySet().iterator(); iterator
						.hasNext();) {
					final String pName = (String) iterator.next();
					boolean pStatus = status.get(pName);
					if (pStatus){
						Model p = DbFunctions.getModelByName(pName, model.getClassTable());
						DbFunctions.delete(p);
						//android.view.ViewRootImpl$CalledFromWrongThreadException: Only the original thread that created a view hierarchy can touch its views.
						runOnUiThread(new Runnable() {							
							@Override
							public void run() {
								//status.remove(pName);
								updateView();					
							}
						});
						cnt++;
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

				  }else*/
				if (cnt==0)
					Toast.makeText(this, "Не выбрано ни одного элемента", 15).show();
				else{
					while(status.containsValue(true)){
						for (Iterator<String> iterator = status.keySet().iterator(); iterator
								.hasNext();) {
							String pName = (String) iterator.next();
							status.remove(pName,true);
						}
					}
					//AlertDialog dialog = new AlertDialog("Удаление завершено");
					//dialog.show(getFragmentManager(), "Сообщение");
				}
			}
			if (val.contentEquals(RENAME)){
				int cnt=0;
				for (Iterator<String> iterator = status.keySet().iterator(); iterator
						.hasNext();) {
					final String pName = (String) iterator.next();
					boolean pStatus = status.get(pName);
					if (pStatus){
						Toast.makeText(this, "Переименование "+pName, 15).show();
						final Editable p = new Editable(DbFunctions
								.getModelByName(pName										
										,model.getClassTable()));
						MySaveDialog dialog = (MySaveDialog)new MySaveDialog().newInstance(pName);
						dialog.activity = this;
						dialog.model = model;
						dialog.p = p;
						dialog.pName = pName;
						dialog.status = status;
						dialog.show(getFragmentManager(),"Переименование");
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
				  }else*/
				  if(cnt==0)
			          Toast.makeText(this, "Не выбрано ни одного элемента", 15).show();
			}
		}
		@Override
		public WebView getVebWebView() {
			return vebViewHome;
		}
		class MyAdapter extends SimpleAdapter{
			
			public MyAdapter(Context context, List<? extends Map<String, ?>> data,
					int resource, String[] from, int[] to) {
				super(context, data, resource, from, to);
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent){
				View view = null;
				System.out.println("------ start MyAdapter.getView");
				if (convertView == null) {
					LayoutInflater inflator = getLayoutInflater();
					view = inflator.inflate(R.layout.row_edit, null);
				}else{
					view = convertView;
				}
				CheckBox checkBox = (CheckBox)view.findViewById(R.id.check);
				TextView text = (TextView)view.findViewById(R.id.text1);
				Map<String,Object> data = (Map<String, Object>) super.getItem(position);
				final String pName = (String)data.get(name);
				checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

					@Override
					public void onCheckedChanged(CompoundButton box,
							boolean isChecked) {
						status.put(pName,box.isChecked());
					}
					
				});
				boolean newStatus = status.get(pName);
				checkBox.setChecked(newStatus);
				text.setText(pName);				
				System.out.println("name:"+pName+" newstatus:"+newStatus);
				System.out.println("------ end MyAdapter.getView");	
				return view;
			}			
		}
}
class Editable{
	private String name;
	Model model;
	public Editable(Model model){
		this.model = model;
		try{name = (String) getField().get(model);
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		try{getField().set(model, name);
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		this.name = name;
	}
	private Field getField(){
		Field field=null;		
		try{field = model.getClass().getField("name");			
		}catch(NoSuchFieldException e){
			e.printStackTrace();
		}catch (SecurityException e) {
			e.printStackTrace();
		}
		return field;
	}
}
class EditModel implements Parcelable{
	int nameId=0,name1Id=0;
	//Class <? extends Model> cls;
	String clsName=null;
	String clsPkg=null;

	private EditModel(Parcel in) {
		nameId = in.readInt();
		name1Id = in.readInt();
		clsName = in.readString();
		clsPkg = in.readString();
	}
	public EditModel() {}
	@Override
	public int describeContents() {
		if (clsName==null||nameId==0||name1Id==0)
			return -1;
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeInt(nameId);
		dest.writeInt(name1Id);
		dest.writeString(clsName);
		dest.writeString(clsPkg);
		//dest.writeTypedObject(cls, 0);
	}
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public EditModel createFromParcel(Parcel in) {
            return new EditModel(in);
        }

        public EditModel[] newArray(int size) {
            return new EditModel[size];
        }
    };
	public Class<? extends Model> getClassTable(){
		String clsName = this.clsPkg+"."+this.clsName;
		clsName = clsName.replace("..", ".");
		Class<? extends Model> cls = null;
		try{cls = (Class<? extends Model>) Class.forName(clsName);		
		}catch (ClassNotFoundException e){
			e.printStackTrace();				
		}
		return  cls;
	}
}

