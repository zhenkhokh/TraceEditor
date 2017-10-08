package ru.zheka.android.timer;

import ru.android.zheka.gmapexample1.PositionInterceptor;
import ru.android.zheka.gmapexample1.PositionUtil;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

public class PositionReciever extends BroadcastReceiver implements Recievable{
	GoogleMap mMap;
	PositionInterceptor position;
	
	public PositionReciever(GoogleMap mMap,PositionInterceptor position){
		this.mMap = mMap;
		this.position = position;
	}

	@Override
	public void onReceive(Context arg0, final Intent intent) {
		System.out.println("recieve location: "+position.getLocation());

		position.target.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {				
				/*Uri data = (Uri)intent.getData();
				PositionUtil util = new PositionUtil();
				try{util.positionAndBoundInit(intent);				
				}catch(Exception e){}
				*/
				if (position==null) return;
				LatLng location = position.getLocation();//util.getCenter();
				System.out.println("recieve location: "+location);
				float zoom = position.zoom;
				if (mMap!=null)
					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
						new CameraPosition.Builder()
						.target(location)
						.zoom(zoom)
						.build()
						));
			}
		});
	}

}
