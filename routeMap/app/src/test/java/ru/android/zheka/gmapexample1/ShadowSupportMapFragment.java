package ru.android.zheka.gmapexample1;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

import ru.android.zheka.gmapexample1.R;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

//@Implements(SupportMapFragment.class)
public class ShadowSupportMapFragment extends  com.google.android.gms.maps.SupportMapFragment{
	static private ShadowSupportMapFragment fragment = new ShadowSupportMapFragment();
	//@Implementation
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//fragment = (SupportMapFragment)getFragmentManager().findFragmentById(R.id.map);
		View view = inflater.inflate((int)R.layout.activity_simple_bundle, container);
		return view;
	}
	
	static public ShadowSupportMapFragment getInstance(){
		return fragment;
	}
}
