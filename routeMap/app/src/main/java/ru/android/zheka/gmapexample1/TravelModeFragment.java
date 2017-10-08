package ru.android.zheka.gmapexample1;

import ru.android.zheka.gmapexample1.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;
import android.app.ListFragment;
import android.app.Fragment.InstantiationException;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TravelModeFragment extends ListFragment {
	int arrayDataID = SettingsActivity.travelArrayDataID;
	int userDataID = SettingsActivity.travelUserDataID;
	String fConfigName = SettingsActivity.travelConfigName;
	Map dataToUser =  new HashMap<String, String>();
	Map userToData =  new HashMap<String, String>();
	private int index=-1;

	public TravelModeFragment(int arrayDataID,int userDataID,String fConfigName){
		this.arrayDataID = arrayDataID;
		this.userDataID = userDataID;
		this.fConfigName = fConfigName;
	}
	public TravelModeFragment(){};
	
	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(SettingsActivity.travelFragmentResId);
		String[] tItems = getResources().getStringArray(arrayDataID);
		String[] uItems = getResources().getStringArray(userDataID);
		if (tItems.length!=uItems.length){
			throw new IndexOutOfBoundsException("tItems.length!=uItems.length");
		}
		for (int i = 0; i < tItems.length; i++) {
			dataToUser.put(tItems[i], uItems[i]);
			userToData.put(uItems[i], tItems[i]);
		}
		//travelIndex = arrTravItims.indexOf((String)config.travelMode);
		Comparator<String> c = new Comparator<String>() {			
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		};
	    Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
	    //arrTravItims.sort(c);
	    Arrays.sort(tItems, c);
	    //tItems = arrTravItims.toArray(new String[0]);
	    String selectedData = null;
	    try{Field field = Config.class.getField(fConfigName);
	    	selectedData = (String) field.get(config);
	    }catch (IllegalAccessException e){e.printStackTrace();
	    }catch (NullPointerException e) {e.printStackTrace();
	    }catch (ExceptionInInitializerError e) {	e.printStackTrace();
	    }catch (IllegalArgumentException e) {	e.printStackTrace();	    
	    }catch (NoSuchFieldException e) { e.printStackTrace();
		}
	    if (selectedData==null)
	    	return;
	    setIndex(Arrays.binarySearch(tItems, selectedData, c));
System.out.println("from "+getClass()+" selectedData is"+selectedData+" index is"+getIndex());
		if (getIndex()<0)
			System.out.println("error to find travelIndex");
		List data = new ArrayList<HashMap<String,?>>();			
		for (int i = 0; i < tItems.length; i++) {
			Map map = new HashMap<String,Object>();
			map.put(SettingsActivity.name1,dataToUser.get(tItems[i]));
			map.put(SettingsActivity.name2,false);
			data.add(map);
		}
		System.out.println("TravelMode data is "+data);
		SettingsActivity a = new SettingsActivity();
		String[] out = {a.name1,a.name2};
		int[] in = {a.item1Id,a.item2Id};
		int res = a.rowResID;
		TravelAdapter adapter = new TravelAdapter(getActivity()
				, data
				, res
				, out
				, in);
		setListAdapter(adapter);
	};
	protected int getIndex(){
		return index;
	}
	protected void setIndex(int index){
		this.index = index;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			android.os.Bundle savedInstanceState) {
		return inflater.inflate(SettingsActivity.fragmentResId, null);
	}
	@Override
	public void onListItemClick(ListView l, View v,int position, long id) {
	    super.onListItemClick(l, v, position, id);
	    setIndex(position);
	    System.out.println("travelMode position:"+position);
	    String data = (String)((Map)getListAdapter()
	    		.getItem(getIndex()))
	    		.get(SettingsActivity.name1);
	    data = (String) userToData.get(data);
	    Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
	    //config.travelMode = data;
	    try{Field field = Config.class.getField(fConfigName);
	    	field.set(config, data);
	    }catch (IllegalAccessException e){e.printStackTrace();
	    }catch (NullPointerException e) {e.printStackTrace();
	    }catch (ExceptionInInitializerError e) {	e.printStackTrace();
	    }catch (IllegalArgumentException e) {	e.printStackTrace();	    
	    }catch (NoSuchFieldException e) {	e.printStackTrace();	    
	    }catch (SecurityException e) {	e.printStackTrace();	    
	    }
	    
	    try{DbFunctions.add(config);
	    }catch(IllegalAccessException e){			e.printStackTrace();
	    }catch (InstantiationException e) {			e.printStackTrace();
		}catch (IllegalArgumentException e) {		e.printStackTrace();
		}catch (Exception e) {	e.printStackTrace();			
		}		    
	    ((BaseAdapter) getListAdapter()).notifyDataSetChanged();
	}
	class TravelAdapter extends SimpleAdapter{

		public TravelAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View view = null;
			if (convertView == null) {
				LayoutInflater inflator = getActivity().getLayoutInflater();
				view = inflator.inflate(SettingsActivity.rowResID, null);
			}else{
				view = convertView;
			}
			RadioButton checkBox = (RadioButton)view.findViewById(SettingsActivity.item2Id);
			TextView text = (TextView)view.findViewById(SettingsActivity.item1Id);
			text.setText((String)((Map)getItem(position)).get(SettingsActivity.name1));
			System.out.println("position in Adapter is "+position+" index is "+getIndex());
			if(getIndex()==position)
				checkBox.setChecked(true);
			else
				checkBox.setChecked(false);
			return view;
		}
	}
}
