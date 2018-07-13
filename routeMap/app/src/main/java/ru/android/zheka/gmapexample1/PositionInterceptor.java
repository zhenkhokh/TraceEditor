package ru.android.zheka.gmapexample1;

import java.util.ArrayList;

import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.gmapexample1.PositionUtil.TRACE_PLOT_STATE;
import ru.android.zheka.route.BellmannFord;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class PositionInterceptor implements ConnectionCallbacks, OnConnectionFailedListener,LocationListener{
	public Marker markerCenter ;
	public LatLng centerPosition=null;;
	public LatLng start=null;
	public LatLng end=null;
	public String title;
	public Float zoom = new Float(PositionUtil.zoomDefault);
	public Activity target;
	public String tracePointName;
	public TRACE_PLOT_STATE state;
	public ArrayList<String> extraPoints=new ArrayList<String>();
	GoogleApiClient mGoogleApiClient=null;
	Location mLastLocation=null;
	static final public int resViewId = R.id.coordinateText; 
	
	interface UIUpdater {
		void updateUILocatin();
	}
	
	public PositionInterceptor(Activity target){
		this.target = target;
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(target)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
            System.out.println("mGoogleApiClient is "+mGoogleApiClient);
            if (mGoogleApiClient!=null)
            	mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
	                mGoogleApiClient);
            System.out.println("mLastLocation is "+mLastLocation);
        }  
	}

	public Intent positioning() throws Exception{
		Intent intent = target.getIntent();
		PositionUtil positionUtil = new PositionUtil();
		//CameraUpdate zoom, center;
		try{positionUtil.positionAndBoundInit(intent);//throws Exception		
		}catch(Exception e){
			centerPosition=getCenter(intent);
			throw e;
		}
		state = positionUtil.defCommand();

		tracePointName=null;
		centerPosition=getCenter(intent);//it's not same as positionUtil.getCenter()
		start = positionUtil.getStart();
		end = positionUtil.getEnd();
		switch (state){
			case CENTER_COMMAND: tracePointName = "";				
			case CENTER_CONNECT_COMMAND: if (tracePointName==null) tracePointName = "next";
			case CENTER_START_COMMAND:  if (tracePointName==null) tracePointName = "start";
			case CENTER_END_COMMAND:  {if (tracePointName==null) tracePointName = "end";
				centerPosition = positionUtil.getCenter();
				break;
			}case CONNECT_COMMAND: tracePointName = "next";
			case END_COMMAND:{ if (tracePointName==null) tracePointName = "end";
				centerPosition = end;
				break;
			}case START_COMMAND:{
				tracePointName = "start";
				centerPosition = start;
				break;
			}case DONOTHING_COMMAND:{
				tracePointName = "center";
				//use getCenter(intent) see above				
			}
		}
		extraPoints = positionUtil.getExtraPoints();
		return intent;				
	}
	public Intent updatePosition(){
		Intent intent = target.getIntent();
		PositionUtil positionUtil = new PositionUtil();
		LatLng center; 
		if (centerPosition!=null)
			center = centerPosition;
		else{
			Float zoomCur = zoom;
			String titleCur = title;
			center = getCenter(intent);
			zoom = zoomCur;
			title = titleCur;
		}
		try{positionUtil.positionAndBoundInit(intent);
			start = positionUtil.getStart();
			end = positionUtil.getEnd();
			state = positionUtil.defCommand();
		}catch(Exception e){System.out.println("start or end point is not exist in intention");
			start = PositionUtil.LAT_LNG;
			end = PositionUtil.LAT_LNG;
			state = TRACE_PLOT_STATE.DONOTHING_COMMAND;
		}
		positionUtil.setCenter(center);
		positionUtil.setZoom(zoom);
		positionUtil.setTitleMarker(title);
		extraPoints = positionUtil.getExtraPoints();
		return positionUtil.getIntent();
	}
	private LatLng getCenter(Intent intent) {
		PositionUtil positionUtil = new PositionUtil();
		Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
				,Config.class);
		try{positionUtil.positionAndBoundInit(intent);
		}catch(Exception e){
			try{positionUtil.setCenterPosition(intent);
        	centerPosition = positionUtil.getCenter();
    		title = positionUtil.getTitleMarker();
    		zoom = positionUtil.getZoom();
	       	if (mLastLocation!=null && config.uLocation)
	       		 centerPosition = getLocation();
    		return centerPosition;
	       	}catch(Exception ee){
	       		Toast.makeText(target, "start point is not specified: задайте местоположение старта" , 15).show();
		       	System.out.println("from centerInit: get geoPosition");
		       	if (mLastLocation!=null){
		       		 return getLocation();
		       	}
				return PositionUtil.getGeoPosition(target);
			}
		}
		centerPosition = positionUtil.getCenter();
		if (mLastLocation!=null && config.uLocation)
      		 centerPosition = getLocation();
		title = positionUtil.getTitleMarker();
		zoom = positionUtil.getZoom();		
		return centerPosition;
	}
	public Intent getNewIntent(){
		PositionUtil util = new PositionUtil();
		state=null;
		try{util.positionAndBoundInit(target.getIntent());
		}catch(Exception e){
			state = TRACE_PLOT_STATE.DONOTHING_COMMAND;
		}
		if(state==null)
			state = util.defCommand();		
		util.setCommand(state);
		util.setCenter(centerPosition);
		util.setStart(start);
		util.setEnd(end);
		util.setZoom(zoom);
		util.setTitleMarker(tracePointName);
		util.setExtraPoints(extraPoints);
		return util.getIntent();
	}
	/*
	 * location
	 * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks#onConnected(android.os.Bundle)
	 */
		@Override
		public void onConnected(Bundle arg0) {
			System.out.println("start ConnectionCallbacks.onConnected");
	        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
	                mGoogleApiClient);
	        if (mLastLocation != null) {
	        	updateUILocation();
	        }
	        System.out.println("mLastConnection is "+mLastLocation);
	        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient
	        		, LocationRequest.create(), this);
			System.out.println("end ConnectionCallbacks.onConnected");	        
		}
		public void updateUILocation(){
			System.out.println("call updateUILocation");
	    	TextView coordinate = (TextView)target.findViewById(resViewId);
	    	if (coordinate!=null){
		    	mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
		                 mGoogleApiClient);
		    	String text = null;
		    	//TODO reduce precision add length
		        if (mLastLocation != null) {
		        	//LatLng location = new LatLng(mLastLocation.getLatitude()
		        	//		, mLastLocation.getLongitude());
		        	//text = location.toString();
					text = String.format ("(%.4f %.4f)",mLastLocation.getLatitude(),mLastLocation.getLongitude());
			        System.out.println("updateUILocation succesfully finished");
		        }else
		        	text = String.format ("(%.4f %.4f)",PositionUtil.LAT_LNG.latitude
							,PositionUtil.LAT_LNG.longitude);
		        	//text = PositionUtil.LAT_LNG.toString();
				Config config  = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
						, Config.class);
				if(config.uLocation){
					StringBuilder sb = new StringBuilder ("ш.д.: ");
					sb.append (text.replace (",","."))
							.append (" длина: ")
							.append (String.format ("%.2f",BellmannFord.length).replace (",","."))
							.append (" км");
					coordinate.setVisibility(View.VISIBLE);
					coordinate.setText(sb.toString ());
				}
		        System.out.println("updateUI coordinate succesfully finished. mLastLocation is "+mLastLocation);
	    	}
		}
	/*
	 * location
	 * @see com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks#onConnectionSuspended(int)
	 */
		@Override
		public void onConnectionSuspended(int arg0) {
			// TODO Auto-generated method stub
		}
	/*
	 * 
	 */
		@Override
		public void onConnectionFailed(ConnectionResult arg0) {
			// TODO Auto-generated method stub		
		}

		@Override
		public void onLocationChanged(Location arg0) {
			if (mLastLocation != null){
				updateUILocation();
				System.out.println("updateUILocatin called in MapsActivity.onLocationChanged");
			}    	
		}

		public LatLng getLocation() {
			if (mLastLocation!=null)
	       		 return new LatLng( mLastLocation.getLatitude()
	       				 , mLastLocation.getLongitude());
	       	System.out.println("mLastLocation is not initialized... try PositionUtil.getGeoPosition");
	       	return PositionUtil.getGeoPosition(target);
		}
}