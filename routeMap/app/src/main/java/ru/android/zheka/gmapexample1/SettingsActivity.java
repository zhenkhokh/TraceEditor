package ru.android.zheka.gmapexample1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.gmapexample1.R;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectView;
import ru.android.zheka.jsbridge.JsCallable;

public class SettingsActivity extends RoboActivity implements JsCallable{
	public static final String HOME = "home";
	@InjectView(R.id.webViewSettings)
	WebView vebViewHome;
	final int layoutResID = R.layout.activity_settings;	
	final int optimViewId = R.id.optimization;
	final int updateViewId = R.id.update;
	final int avoidId = R.id.avoid;

	final static int fragmentResId = R.layout.fragment_list;
	final static int rowResID = R.layout.row_single_choise;	 
	final static int travelArrayDataID = R.array.travelmodelist;
	final static int travelUserDataID = R.array.traveluserlist;
	final static String travelConfigName = "travelMode";
	final static int item1Id = R.id.text;
	final static int item2Id = R.id.radio;
	final static String name1 = "text";
	final static String name2 = "radio";
	
	final static int timerArrayDataID = R.array.timerdatalist;
	final static int timerUserDataID = R.array.timeruserlist;
	final static String timerConfigName = "tenMSTime";
	
	String url = "file:///android_asset/settings.html";
	
	@Override
	public void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("befor setContentView(layoutResID)");
		setContentView(layoutResID);
		System.out.println("after setContentView(layoutResID)");

		TravelModeFragment travel = new TravelModeFragment();
		//FragmentManager fm = getFragmentManager();
		//FragmentTransaction transaction =  fm.beginTransaction();
		//transaction.add(travel,"list");
		//transaction.commit();
		Switch optimization = (Switch)findViewById(optimViewId);
		final Switch update = (Switch)findViewById(updateViewId);
		Switch avoidTolls = (Switch)findViewById(avoidId);
	    Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
	    optimization.setChecked(config.optimization);
	    update.setChecked(config.uLocation);
	    avoidTolls.setChecked(config.avoid.contains(DbFunctions.AVOID_TOLLS));
	    
		optimization.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			    Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
			    boolean optimization = false;
				if (arg0.isChecked())
					optimization = true;
				else
					optimization = false;
				config.optimization = optimization;
			    try{DbFunctions.add(config);		    
			    }catch(IllegalAccessException e){			e.printStackTrace();
			    }catch (InstantiationException e) {			e.printStackTrace();
				}catch (IllegalArgumentException e) {		e.printStackTrace();
				}catch (Exception e) {	e.printStackTrace();			
				}
			    System.out.println("optimisation set as "+optimization); 
			}
		});
		update.setOnCheckedChangeListener(new OnCheckedChangeListener() {			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			    Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
			    boolean update = false;
				if (arg0.isChecked())
					update = true;
				else
					update = false;
				config.uLocation = update;
			    try{DbFunctions.add(config);		    
			    }catch(IllegalAccessException e){			e.printStackTrace();
			    }catch (InstantiationException e) {			e.printStackTrace();
				}catch (IllegalArgumentException e) {		e.printStackTrace();
				}catch (Exception e) {	e.printStackTrace();			
				}
			    System.out.println("update location is set as "+update);
			}
		});
		avoidTolls.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
			    Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
				if (arg0.isChecked()) config.avoid = push(config.avoid, DbFunctions.AVOID_TOLLS);
				else config.avoid = pull(config.avoid,DbFunctions.AVOID_TOLLS);
				
			    try{DbFunctions.add(config);		    
			    }catch(IllegalAccessException e){			e.printStackTrace();
			    }catch (InstantiationException e) {			e.printStackTrace();
				}catch (IllegalArgumentException e) {		e.printStackTrace();
				}catch (Exception e) {	e.printStackTrace();			
				}
			    System.out.println("avoid tolls is set as "+config.avoid);
			}
		});
		
        MenuHandler m = new MenuHandler();
        m.initJsBridge(this, url);
	};
	
	@Override
	public void nextView(String val) {
		Intent intent = getIntent();
		if (val.contentEquals(HOME)) {
            Toast.makeText(this, "Home view called " + val, 15).show();
            intent.setClass(this, MainActivity.class);
	        intent.setAction(Intent.ACTION_VIEW);
            startActivity(intent);
            finish();	
		}
	}
	@Override
	public WebView getVebWebView() {
		return vebViewHome;
	}
	static public String push(String pipeline, String in){
		String[] items = null;
		if (in.isEmpty())
			return pipeline;
		if (!pipeline.contains("|")){
			if(!pipeline.contentEquals(in)){
				if (!pipeline.isEmpty()) pipeline = pipeline.concat("|"+in);
				else pipeline = in;
				return pipeline;
			}else 	return in;// or do nothing
		}else{
			items = pipeline.split("\\|");		
			//}catch(PatternSyntaxException e){	throw e;	
			//}
			int i = Arrays.binarySearch(items, in,	new Comparator<String>() {			
				@Override
				public int compare(String o1, String o2) {
					return o1.compareTo(o2);
				}
			});			
			if (i==-1){
				if (items.length>0 && !items[items.length-1].isEmpty())
					pipeline = pipeline.concat("|"+in);//add to end
				else
					pipeline = in;
				return pipeline;
			}
		}
		return pipeline;
	}
	static public String pull(String pipeline, String in){
		String[] items = null;
		if (in.isEmpty())
			return pipeline;
		pipeline = pipeline.replace("||", "");// remove empty items
		if (!pipeline.contains("|")){
			return pipeline.replace(in, "");
		}else{
			//Pattern pattern = Pattern.compile("\\|");
			//items = pattern.split(pipeline);
			try{items = pipeline.split("\\|");
			}catch(PatternSyntaxException e){	throw e;	
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < items.length; i++) {
				if (!items[i].contentEquals(in)){
					if (sb.length()!=0) sb.append("|").append(items[i]); 
					else sb.append(items[i]);
				}
			}
			return sb.toString();
		}
	}
}

