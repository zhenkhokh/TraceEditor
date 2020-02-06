package ru.android.zheka.gmapexample1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import roboguice.activity.RoboActivity;
import roboguice.activity.RoboListActivity;
import roboguice.inject.InjectView;
import ru.android.zheka.jsbridge.JsCallable;

public class SettingsActivity extends AppCompatActivity
		//RoboActivity
		implements JsCallable{
	public static final String HOME = "home";
	@InjectView(R.id.webViewSettings)
	WebView vebViewHome;
	final int layoutResID = R.layout.activity_settings;	
	final int optimViewId = R.id.optimizationRadio;
	final int updateViewId = R.id.update;
	final int avoidId = R.id.avoid;
	final int offsetId = R.id.offline;

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
		RadioGroup optimization = (RadioGroup) findViewById(optimViewId);
		RadioButton no = (RadioButton)findViewById (R.id.optimizationNo);
		RadioButton google = (RadioButton)findViewById (R.id.optimizationGoogle);
		RadioButton bellman = (RadioButton)findViewById (R.id.optimizationBellmanFord);
		Spinner spinner = (Spinner)findViewById (R.id.speedSpinner);
		no.setOnClickListener (new OnClickListener () {
			@Override
			public void onClick(View v) {

			}
		});
		google.setOnClickListener (new OnClickListener () {
			@Override
			public void onClick(View v) {

			}
		});
		bellman.setOnClickListener (new OnClickListener () {
			@Override
			public void onClick(View v) {

			}
		});
		final Switch update = (Switch)findViewById(updateViewId);
		Switch avoidTolls = (Switch)findViewById(avoidId);
		Switch offline = (Switch)findViewById(offsetId);

	    Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
	    //optimization.setChecked(config.optimization);
		int radioId = 0;
		if (config.bellmanFord.equals (getResources().getString (R.string.optimizationdata3)))
			radioId = R.id.optimizationBellmanFord;
		else if (config.optimization)
			radioId = R.id.optimizationGoogle;
		else
			radioId = R.id.optimizationNo;
		RadioButton radioButton = (RadioButton)findViewById (radioId);
		radioButton.setChecked (true);
	    update.setChecked(config.uLocation);
	    avoidTolls.setChecked(config.avoid.contains(DbFunctions.AVOID_TOLLS));
	    offline.setChecked (config.offline.equals (getString (R.string.offlineOn)));
	    int pos=0;
	    final String[] choose = getResources().getStringArray(R.array.speedList);
	    String value = config.rateLimit_ms;
		value = new Double (Double.valueOf (value)/1000.0).toString ();
		while(pos<choose.length)
			if (new Double (value).equals(Double.valueOf (choose[pos++])))
				break;
	    spinner.setSelection (pos-1);
		optimization.setOnCheckedChangeListener (new RadioGroup.OnCheckedChangeListener () {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
			    Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
			    boolean optimization = false;
			    if (group.getCheckedRadioButtonId () == checkedId)
					System.out.println ("optimization.setOnCheckedChangeListene: chekId is ok");
			    else
					System.err.println ("optimization.setOnCheckedChangeListene: chekId is incorrect");
			    String optimizationBellman = getResources().getString (R.string.optimizationdata3);
			    //RadioButton checked = (RadioButton) findViewById(checkedId);
				if (checkedId==R.id.optimizationGoogle) {
					optimization = true;
					config.bellmanFord = "";
				}else if (checkedId==R.id.optimizationNo){
					config.bellmanFord = "";
				}else if (checkedId==R.id.optimizationBellmanFord ){
					config.bellmanFord = optimizationBellman;
				}
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

		offline.setOnCheckedChangeListener (new OnCheckedChangeListener () {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
				if (buttonView.isChecked ())
					config.offline = getString (R.string.offlineOn);
				else
					config.offline = getString (R.string.offlineOff);
				System.out.println("offset is set as "+config.offline);
				try{DbFunctions.add(config);
			    }catch(IllegalAccessException e){			e.printStackTrace();
			    }catch (InstantiationException e) {			e.printStackTrace();
				}catch (IllegalArgumentException e) {		e.printStackTrace();
				}catch (Exception e) {	e.printStackTrace();
				}
			}
		});
		spinner.setOnItemSelectedListener (new AdapterView.OnItemSelectedListener (){
			@Override
			public void onItemSelected(AdapterView <?> parent, View view, int position, long id) {
					String[] choose = getResources().getStringArray(R.array.speedList);
					String value = choose[position];
					Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME, Config.class);
					config.rateLimit_ms = new Double(Double.valueOf (value)*1000).toString ();
					try{DbFunctions.add(config);
					}catch(IllegalAccessException e){			e.printStackTrace();
					}catch (InstantiationException e) {			e.printStackTrace();
					}catch (IllegalArgumentException e) {		e.printStackTrace();
					}catch (Exception e) {	e.printStackTrace();
					}
				}
			@Override
			public void onNothingSelected(AdapterView <?> parent) {

			}
		});

//        MenuHandler m = new MenuHandler();
//        m.initJsBridge(this, url);
	};
	
	@Override
	public void nextView(String val) {
		Intent intent = getIntent();
		if (val.contentEquals(HOME)) {
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

