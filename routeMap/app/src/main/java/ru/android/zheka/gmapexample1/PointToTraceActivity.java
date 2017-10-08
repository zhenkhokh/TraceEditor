package ru.android.zheka.gmapexample1;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class PointToTraceActivity extends LatLngActivity{
	//public PointToTraceActivity(){
	//	super.cls = TraceActivity.class;
	//}
	@Override
	public void onCreate(Bundle saved){
		super.onCreate(saved);
        try {
    	    cls = Class.forName("ru.android.zheka.gmapexample1.TraceActivity");
    	    System.out.println("----------  from PointToTraceActivity: find  ru.android.zheka.gmapexample1.TraceActivity");
    	}catch (ClassNotFoundException e){
    	    System.out.println("---------- from PointToTraceActivity "+e.getMessage());
    	}
	}
	//@Override
	//public void onListItemClick(ListView l, View v,int position, long id) {
	
	//}
}
