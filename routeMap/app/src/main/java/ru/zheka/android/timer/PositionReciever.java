package ru.zheka.android.timer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.gmapexample1.MapsActivity;
import ru.android.zheka.gmapexample1.PositionInterceptor;
import ru.android.zheka.gmapexample1.PositionUtil;
import ru.android.zheka.route.BellmannFord;

public class PositionReciever extends BroadcastReceiver implements Recievable {
    GoogleMap mMap;
    PositionInterceptor position;
    Config config;
    boolean geoMode = false;
    MapsActivity mapsActivity;

    public PositionReciever(GoogleMap mMap, PositionInterceptor position) {
        this.mMap = mMap;
        this.position = position;
        config = (Config) DbFunctions.getModelByName (DbFunctions.DEFAULT_CONFIG_NAME
                , Config.class);
        //this.position.state = PositionUtil.TRACE_PLOT_STATE.CENTER_START_COMMAND;
    }

    public PositionReciever(GoogleMap mMap, PositionInterceptor position, MapsActivity mapsActivity) {
        this (mMap, position);
        this.geoMode = true;
        this.mapsActivity = mapsActivity;
    }

    @Override
    public void onReceive(Context arg0, final Intent intent) {
        System.out.println ("recieve location: " + position.getLocation ());

        position.target.runOnUiThread (new Runnable () {
            LatLng location = null;

            @Override
            public void run() {
				/*Uri data = (Uri)intent.getData();
				PositionUtil util = new PositionUtil();
				try{util.positionAndBoundInit(intent);				
				}catch(Exception e){}
				*/
                //TODO update service
				/*
				if (position==null) return;
				LocationCallback mLocationCallback = new LocationCallback (){
					@Override
        			public void onLocationResult(LocationResult locationResult) {
							if (locationResult==null)
								return;
							locationResult.getLastLocation ();
							Location lastLocation = locationResult.getLastLocation ();
							location = new LatLng (lastLocation.getLatitude (),lastLocation.getLongitude ());
							Iterator<Location> iterator = locationResult.getLocations ().iterator ();
							while(iterator.hasNext ())
								System.out.println ("location_"+iterator.next ());
					}
				};
				LocationServices.getFusedLocationProviderClient (position.target).requestLocationUpdates (
						LocationRequest.create ().setPriority (LocationRequest.PRIORITY_HIGH_ACCURACY)
						,mLocationCallback
						,Looper.myLooper ()
				);
				*/
                if (location == null) {
                    location = position.getLocation ();//util.getCenter();
                }
                if (location.equals (PositionUtil.LAT_LNG)) {
                    position.initLocation ();
                    location = position.getLocation ();
                }
                //position.start = location;
                position.centerPosition = location;
                position.target.setIntent (position.getNewIntent ());//order is important
                //synchronized (PositionReciever.this.position){
                //LatLng end = position.end;
                position.updatePosition ();//for manual saving
                //position.end = end;//buggy end
                //}
				/*if (location!=null && position.extraPoints.size ()>0)
					position.extraPoints.set (0,(String) new UtilePointSerializer ().serialize (location));
				*/
                if (config.uLocation)
                    position.updateUILocation ();
                System.out.println ("recieve location: " + location);
                float zoom = position.zoom;
                if (mMap != null) {
                    float bearing = 0;
                    if (geoMode) {
                        mapsActivity.goPosition (true);
                        if (BellmannFord.bearing != Float.NaN)
                            bearing = BellmannFord.bearing;
                    }
                    mMap.animateCamera (CameraUpdateFactory.newCameraPosition (
                            new CameraPosition.Builder ()
                                    .target (location)
                                    .bearing (bearing)
                                    .zoom (zoom)
                                    .build ()
                    ));
                }
            }
        });
				/*
				if (geoMode && mapsActivity!=null && mapsActivity.dataTrace!=null
				 		&& position.start!=null	&& position.end!=null) {
					Trace trace = new Trace();
					trace.data = mapsActivity.dataTrace;
					trace.start = position.start;
					trace.end = position.end;
					trace.name = "emergency_saving";
					try {
						DbFunctions.add (trace);
					}catch(java.lang.InstantiationException e){
						e.printStackTrace();
					}catch(IllegalAccessException e){
						e.printStackTrace();
					}catch(IllegalArgumentException e){
						e.printStackTrace();
					}
				}
				*/
    }

}
