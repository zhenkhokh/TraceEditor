package ru.android.zheka.gmapexample1;

import java.util.ArrayList;

import ru.android.zheka.db.UtilePointSerializer;
import ru.android.zheka.route.BellmannFord;

import android.Manifest;
import android.R.bool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class PositionUtil {
	public static final String EXTRA_POINTS = "extraPoints";
	public final static float zoomDefault = 10;
	public final static String titleDefault = "";
	public static final LatLng LAT_LNG = new LatLng (55.9823964, 37.1690829);
	public static boolean isCenterAddedToTrace = false;
	LatLng center, start, end;
	float zoom = 0;
	boolean startPass = false, endPass = false, centerPass = false;
	//TODO add title to Uri
	String titleMarker = null;
	Intent intent;
	ArrayList <String> extraPoints = new ArrayList <String> ();

	public enum TRACE_PLOT_STATE {
		END_COMMAND, START_COMMAND, CONNECT_COMMAND, CENTER_COMMAND, CENTER_END_COMMAND, CENTER_START_COMMAND, CENTER_CONNECT_COMMAND, DONOTHING_COMMAND
	}

	;

	public Intent getIntent() {
		intent = new Intent ();
		Uri uri;
		if (center != null && zoom > 1) {
			if (titleMarker != null) {
				uri = Uri.parse (getGeoPositionFalsePass (center, String.valueOf (zoom), titleMarker));
			} else
				uri = Uri.parse (getGeoPositionFalsePass (center, String.valueOf (zoom)));
			try {
				uri = setUpPassParam (uri, centerPass);
			} catch (Exception e) {
				System.out.println ("failure");
			}
			;
			intent.setData (uri);
		}
		if (start != null) {
			uri = Uri.parse (getTracePointFalsePass (start, "start"));
			try {
				uri = setUpPassParam (uri, startPass);
			} catch (Exception e) {
			}
			;
			intent.putExtra ("start", uri);
		}
		if (end != null) {
			uri = Uri.parse (getTracePointFalsePass (end, "end"));
			try {
				uri = setUpPassParam (uri, endPass);
			} catch (Exception e) {
			}
			;
			intent.putExtra ("end", uri);
		}
		if (!extraPoints.isEmpty ()) {
			intent.putStringArrayListExtra (EXTRA_POINTS, extraPoints);
		}
		return intent;
	}

	public void setCommand(TRACE_PLOT_STATE command) {
		switch (command) {
			case START_COMMAND: {
				startPass = true;
				endPass = false;
				centerPass = false;
				break;
			}
			case CONNECT_COMMAND: {
				startPass = true;
				endPass = true;
				centerPass = false;
				break;
			}
			case CENTER_START_COMMAND: {
				startPass = true;
				endPass = false;
				centerPass = true;
				break;
			}
			case CENTER_CONNECT_COMMAND: {
				startPass = true;
				endPass = true;
				centerPass = true;
				break;
			}
			case END_COMMAND: {
				startPass = false;
				endPass = true;
				centerPass = false;
				break;
			}
			case CENTER_END_COMMAND: {
				startPass = false;
				endPass = true;
				centerPass = true;
				break;
			}
			case CENTER_COMMAND: {
				startPass = false;
				endPass = false;
				centerPass = true;
				break;
			}
			case DONOTHING_COMMAND: {
				startPass = false;
				endPass = false;
				centerPass = false;
				break;
			}
		}
	}

	public void setCenterPosition(Intent intent) throws Exception {
		Uri uri = (Uri) intent.getData ();
		Float latitude = null, longitude = null;
		String[] s = uri.getSchemeSpecificPart ()
				.split (",");
		final String queres = uri.getQuery ();
		if (s.length < 2)
			throw new ArrayIndexOutOfBoundsException ();
		try {
			latitude = new Float (s[0]);
			longitude = new Float (s[1].replace (queres, "")
					.replace ("?", ""));
		} catch (NumberFormatException e) {
			String msg = "geo" + " latitude is not float format";
			if (latitude == null)
				throw new Exception (msg);
			if (longitude == null)
				throw new Exception (msg);
		}
		try {
			String sZoom = getQueryParameter (uri, "z");
			this.zoom = new Float (sZoom);
		} catch (NumberFormatException e) {
			this.zoom = zoomDefault;
		}
		titleMarker = getQueryParameter (uri, "title");
		center = new LatLng (latitude, longitude);
		if (intent.getStringArrayListExtra (EXTRA_POINTS) != null)
			extraPoints = intent.getStringArrayListExtra (EXTRA_POINTS);
	}

	public ArrayList <String> getExtraPoints() {
		return extraPoints;
	}

	public void setExtraPoints(ArrayList <String> extraPoints) {
		this.extraPoints = extraPoints;
	}

	public LatLng getCenter() {
		return center;
	}

	public void setCenter(LatLng center) {
		this.center = center;
	}

	public LatLng getStart() {
		return start;
	}

	public void setStart(LatLng start) {
		this.start = start;
	}

	public LatLng getEnd() {
		return end;
	}

	public void setEnd(LatLng end) {
		this.end = end;
	}

	public float getZoom() {
		return zoom;
	}

	public void setZoom(float zoom) {
		this.zoom = zoom;
	}

	public boolean getStartPass() {
		return startPass;

	}

	public void setStartPass(boolean startPass) {
		this.startPass = startPass;
	}

	public void setEndPass(boolean endPass) {
		this.endPass = endPass;
	}

	public boolean getEndPass() {
		return endPass;
	}

	public boolean getCenterPass() {
		return centerPass;
	}

	public void setCenterPass(boolean centerPass) {
		this.centerPass = centerPass;
	}

	public void setTitleMarker(String titleMarker) {
		this.titleMarker = titleMarker;
	}

	public String getTitleMarker() {
		return titleMarker;
	}

	private static String getGeoPositionFalsePass(LatLng point, String... zommAndTitle) {
		StringBuilder sb = new StringBuilder ();
		sb.append ("geo:")
				.append (point.latitude)
				.append (",")
				.append (point.longitude)
				.append ("?z=")
				.append (zommAndTitle[0])
				.append ("?pass=false");
		if (zommAndTitle.length > 1) {
			sb.append ("?title=")
					.append (zommAndTitle[1]);
		}
		return sb.toString ();
	}

	static public String getTracePointFalsePass(LatLng point, String name) {
		StringBuilder sb = new StringBuilder ();
		return sb.append (name)
				.append (":")
				.append (point.latitude)
				.append (",")
				.append (point.longitude)
				.append ("?pass=false")
				.toString ();
	}

	public void positionAndBoundInit(Intent intent) throws Exception {
		Uri uriCenter = intent.getData ();
		if (uriCenter == null) {
			System.out.println ("center is not defined");
			throw new Exception ("center is not defined");
		}
		Float latitude = null, longitude = null, zoom = null;
        /*
        String latlng = uriCenter.getSchemeSpecificPart();
        if (latlng==null){
        	throw new NullPointerException();
        }
		String[] s = latlng.split(",");
		if (s.length<2){
			throw new ArrayIndexOutOfBoundsException();
		}
		String[] ss = s[1].split("?");
		if (ss.length<2){
			throw new ArrayIndexOutOfBoundsException();
		}			
		try {
			latitude = new Float(s[0]);
			longitude = new Float(ss[0]);
			zoom = new Float(ss[1].replace("z=", ""));
			if (ss.length==3)
				titleMarker = ss[2];
		}catch(NumberFormatException e){
			if (zoom==null)
				throw new Exception("zoom is not float format");
			if(latitude==null)
				throw new Exception("Center latitude is not float format");
			if(longitude==null)
				throw new Exception("Center longitude is not float format");
		}
		this.zoom = zoom;
		center = new LatLng(latitude, longitude);
		*/
		Uri startUri = null, endUri = null;
		// Uri extra data: start[end]:lat,lng?query
		String[] bound = {"start", "end", "geo"};
		for (int i = 0; i < 3; i++) {
			String name = bound[i];
			Uri uri = null;
			boolean ok = true;
			if (i < 2) {
				if ((Uri) intent.getParcelableExtra (name) != null)// TODO java.lang.ClassCastException: java.lang.String cannot be cast
					//to android.os.Parcelable
					uri = (Uri) intent.getParcelableExtra (name);
				else
					ok = false;
			} else if ((Uri) intent.getData () != null)
				uri = (Uri) intent.getData ();
			else
				ok = false;
			if (ok) {
				String[] s = uri.getSchemeSpecificPart ()
						.split (",");
				final String queres = uri.getQuery ();
				if (s.length < 2)
					throw new ArrayIndexOutOfBoundsException ();
				try {
					latitude = new Float (s[0]);
					longitude = new Float (s[1].replace (queres, "")
							.replace ("?", ""));
				} catch (NumberFormatException e) {
					if (latitude == null)
						throw new Exception (name + " latitude is not float format");
					if (longitude == null)
						throw new Exception (name + " longitude is not float format");
				}
				if (i == 0) {
					start = new LatLng (latitude, longitude);
					startUri = uri;
				}
				if (i == 1) {
					end = new LatLng (latitude, longitude);
					endUri = uri;
				}
				if (i == 2) {
					try {
						String sZoom = getQueryParameter (uri, "z");
						this.zoom = new Float (sZoom);
					} catch (NumberFormatException e) {
						this.zoom = zoomDefault;
					}
					try {
						titleMarker = getQueryParameter (uri, "title");
					} catch (Exception e) {
						titleMarker = titleDefault;
					}
					center = new LatLng (latitude, longitude);
					uriCenter = uri;
				}
			}
		}
		if (intent.getStringArrayListExtra (EXTRA_POINTS) != null)
			extraPoints = intent.getStringArrayListExtra (EXTRA_POINTS);
		centerPass = getPassParam (uriCenter);
		try {
			startPass = getPassParam (startUri);
		} catch (Exception e) {
			startUri = uriCenter;
			startPass = getPassParam (startUri);
		}
		try {
			endPass = getPassParam (endUri);
		} catch (Exception e) {/*endUri = startUri; endPass = getPassParam(endUri);*/
			endPass = false;// do not do CENTER_CONNECT
			if (extraPoints.size () > 1) {
				end = (LatLng) new UtilePointSerializer ().deserialize (extraPoints.get (extraPoints.size () - 1));
			} else {
				throw new NullPointerException ("end point is undefined");
			}
		}

		// construct trace with center=start case, for adding
		//TODO do not replace some points
		/*
		if (center!=null && isCenterAddedToTrace == false
				&& extraPoints.size () >0) {
			if (BellmannFord.round ((LatLng) new UtilePointSerializer ().deserialize (extraPoints.get(0))).equals (
					BellmannFord.round(center)
			)) 	extraPoints.set (0,(String)new UtilePointSerializer ().serialize (center));
			else
				extraPoints.add(0,(String)new UtilePointSerializer ().serialize (center));
			isCenterAddedToTrace = true;
		}else if (center!=null && isCenterAddedToTrace == true && extraPoints.size ()>0)
			extraPoints.set (0,(String)new UtilePointSerializer ().serialize (center));//start
		*/
	}

	static private String getQueryParameter(Uri uri, String name) {
		String queres = uri.getQuery ();
		if (queres.lastIndexOf (name + "=") == -1)
			return null;
		int start = queres.lastIndexOf (name + "=") + name.length () + 1;
		int end = queres.indexOf ("?", start) == -1 ? queres.length () : queres.indexOf ("?", start);
		return queres.substring (start, end);
	}

	static private boolean getPassParam(final Uri uri) throws Exception {
		if (uri != null) {
			String passString = getQueryParameter (uri, "pass");
			if (passString != null) {
				if (passString.equalsIgnoreCase ("true"))
					return true;
				if (passString.equalsIgnoreCase ("false"))
					return false;
			}
		} else
			throw new NullPointerException ("its not possible");
		throw new Exception ("not expected uri: " + uri.toString ());
	}

	static private Uri setUpPassParam(Uri uri, boolean pass) throws Exception {
		if (uri != null) {
			String val = getQueryParameter (uri, "pass");
			String uriString = uri.toString ();
			if (uriString != null) {
				//add pass if not specified
				if (val == null) {
					uriString.concat ("?pass=false");
					val = "false";
				}
			} else
				throw new NullPointerException ("uri cant be a string: specify it correctly");

			String newVal = pass ? "true" : "false";
			uriString = uriString.replaceFirst ("pass=" + val, "pass=" + newVal);
			return Uri.parse (uriString);
		}
		throw new Exception ("cant up pass parameter: uri is null");
	}

	static private Uri createDataAndSetUpTrue(Uri uri, String point) throws Exception {
		uri = setUpPassParam (uri, true);
		String fAppr = uri.getSchemeSpecificPart ();
		String[] s = fAppr.split ("?");
		String oData = s[0];
		fAppr = fAppr.replace (oData, point);
		return Uri.parse (fAppr);
	}

	static public Intent createIntentForExistingCenterEndStart(TRACE_PLOT_STATE states, String pointCenter, Intent in) throws Exception {
		PositionUtil util = new PositionUtil ();
		util.positionAndBoundInit (in);
		util.setCommand (states);
		LatLng center = (LatLng) new UtilePointSerializer ().deserialize (pointCenter);
		switch (states) {
			case START_COMMAND: {
				util.setStart (center);
				break;
			}
			case END_COMMAND: {
				util.setEnd (center);
				break;
			}
			case CONNECT_COMMAND: //same with CENTER_CONNECT_COMMAND
			case CENTER_CONNECT_COMMAND: {
				LatLng start = util.getEnd ();
				util.setEnd (center);
				util.setStart (start);
				break;
			}
			case CENTER_START_COMMAND: {
				util.setStart (center);
				util.setCenter (center);
				break;
			}
			case CENTER_END_COMMAND: {
				util.setEnd (center);
				util.setCenter (center);
				break;
			}
			case CENTER_COMMAND: {
				util.setCenter (center);
				break;
			}
		}
		return util.getIntent ();
    	/*
     	Intent out = in;
    	Uri uriCenter = (Uri)in.getData();
    	Uri uriEnd = (Uri)in.getParcelableExtra("end");
    	Uri uriStart = (Uri)in.getParcelableExtra("start");

    	// set up false
    	uriCenter = setUpPassParam(uriCenter, false);
    	uriEnd = setUpPassParam(uriEnd, false);
    	uriStart = setUpPassParam(uriStart, false);
    	out.setData(uriCenter);
		out.putExtra("start", uriStart);
		out.putExtra("end", uriEnd);
		// and then set up true when it necessary
    	switch (states){
    		case START_COMMAND:{ 
    			uriStart = createDataAndSetUpTrue(uriStart, point);
    			out.putExtra("start", uriStart);
    			break;
    		}case END_COMMAND:{
    			uriEnd = createDataAndSetUpTrue(uriEnd, point);
    			out.putExtra("end", uriEnd);
    			break;
    		}case CONNECT_COMMAND:{
    			out.putExtra("start", uriEnd);
    			uriEnd = createDataAndSetUpTrue(uriEnd, point);
    			out.putExtra("end", uriEnd);
    			break;
    		}case CENTER_CONNECT_COMMAND:{
    			out.putExtra("start", uriEnd);
    			uriEnd = createDataAndSetUpTrue(uriEnd, point);
    			out.putExtra("end", uriEnd);
    			out.setData(uriEnd);
    			break;
    		}case CENTER_START_COMMAND:{
    			uriStart = createDataAndSetUpTrue(uriStart, point);
    			out.putExtra("start", uriStart);
    			out.setData(uriStart);
    			break;
    		}case CENTER_END_COMMAND:{
    			uriEnd = createDataAndSetUpTrue(uriEnd, point);
    			out.putExtra("end", uriEnd);
    			out.setData(uriEnd);
    			break;
    		}case CENTER_COMMAND:{
    	    	uriCenter = (Uri)in.getData();
    			uriCenter = createDataAndSetUpTrue(uriCenter, point);
    			out.setData(uriCenter);
    			break;
    		}case DONOTHING_COMMAND:{
    			break;
    		}
    	}
    	return out;
    	*/
	}

	static public Intent setDefCommand(Intent intent, TRACE_PLOT_STATE state) throws Exception {
		// set all false
		if (intent.getParcelableExtra ("start") != null)
			intent = createIntentForExistingCenterEndStart (state, ((Uri) intent.getParcelableExtra ("start")).toString (), intent);
		if (intent.getParcelableExtra ("end") != null)
			intent = createIntentForExistingCenterEndStart (state, ((Uri) intent.getParcelableExtra ("end")).toString (), intent);
		if (intent.getData () != null)
			intent = createIntentForExistingCenterEndStart (state, ((Uri) intent.getData ()).toString (), intent);
		switch (state) {
			case START_COMMAND: {
				if (intent.getParcelableExtra ("start") != null)
					intent.putExtra ("start", setUpPassParam ((Uri) intent.getParcelableExtra ("start"), true));
				else
					throw new Exception ("bad intent for START_COMMAND");
				break;
			}
			case END_COMMAND: {
				if (intent.getParcelableExtra ("end") != null)
					intent.putExtra ("end", setUpPassParam ((Uri) intent.getParcelableExtra ("end"), true));
				else
					throw new Exception ("bad intent for END_COMMAND");
				break;
			}
			case CONNECT_COMMAND: {
				if (intent.getParcelableExtra ("end") != null
						&& intent.getParcelableExtra ("start") != null) {
					intent.putExtra ("end", setUpPassParam ((Uri) intent.getParcelableExtra ("end"), true));
					intent.putExtra ("start", setUpPassParam ((Uri) intent.getParcelableExtra ("start"), true));
					//do not exchange
				} else
					throw new Exception ("bad intent for CONNECT_COMMAND");
				break;
			}
			case CENTER_CONNECT_COMMAND: {
				if (intent.getParcelableExtra ("end") != null
						&& intent.getParcelableExtra ("start") != null
						&& intent.getData () != null) {
					intent.putExtra ("end", setUpPassParam ((Uri) intent.getParcelableExtra ("end"), true));
					intent.putExtra ("start", setUpPassParam ((Uri) intent.getParcelableExtra ("start"), true));
					intent.setData (setUpPassParam ((Uri) intent.getData (), true));
				} else
					throw new Exception ("bad intent for CENTER_CONNECT_COMMAND");
				break;
			}
			case CENTER_START_COMMAND: {
				if (intent.getParcelableExtra ("start") != null
						&& intent.getData () != null) {
					intent.putExtra ("start", setUpPassParam ((Uri) intent.getParcelableExtra ("start"), true));
					intent.setData (setUpPassParam ((Uri) intent.getData (), true));
				} else
					throw new Exception ("bad intent for CENTER_START_COMMAND");
				break;
			}
			case CENTER_END_COMMAND: {
				if (intent.getParcelableExtra ("end") != null
						&& intent.getData () != null) {
					intent.putExtra ("start", setUpPassParam ((Uri) intent.getParcelableExtra ("start"), true));
					intent.setData (setUpPassParam ((Uri) intent.getData (), true));
				} else
					throw new Exception ("bad intent for CENTER_END_COMMAND");
				break;
			}
			case CENTER_COMMAND: {
				if (intent.getData () != null) {
					intent.setData (setUpPassParam ((Uri) intent.getData (), true));
				} else
					throw new Exception ("bad intent for CENTER_COMMAND");
				break;
			}
			case DONOTHING_COMMAND: {
				break;
			}
		}
		return intent;
	}

	public TRACE_PLOT_STATE defCommand() {
		if (startPass == true && endPass == false && centerPass == false)
			return TRACE_PLOT_STATE.START_COMMAND;
		if (startPass == true && endPass == true && centerPass == false)
			return TRACE_PLOT_STATE.CONNECT_COMMAND;
		if (startPass == true && endPass == false && centerPass == true)
			return TRACE_PLOT_STATE.CENTER_START_COMMAND;
		if (startPass == true && endPass == true && centerPass == true)
			return TRACE_PLOT_STATE.CENTER_CONNECT_COMMAND;
		if (startPass == false && endPass == true && centerPass == false)
			return TRACE_PLOT_STATE.END_COMMAND;
		if (startPass == false && endPass == true && centerPass == true)
			return TRACE_PLOT_STATE.CENTER_END_COMMAND;
		if (startPass == false && endPass == false && centerPass == true)
			return TRACE_PLOT_STATE.CENTER_COMMAND;
		// if all are false
		return TRACE_PLOT_STATE.DONOTHING_COMMAND;
	}

	public static String latLngToString(LatLng point) {
		return new StringBuilder ().append (String.valueOf (point.latitude))
				.append (",")
				.append (String.valueOf (point.longitude))
				.toString ();
	}

	static public LatLng getGeoPosition(Activity activity) {
		LocationManager lManager = (LocationManager) activity.getSystemService (Context.LOCATION_SERVICE);
		Location location = getLastBestLocation (lManager,activity);
		LatLng center = null;
		if (location != null)
			center = new LatLng (location.getLatitude ()
					, location.getLongitude ());
		else {
			center = LAT_LNG;
			System.out.println ("----- location is null get " + LAT_LNG + " instead");
		}
		return center;
	}
private static int permissionCode=1;

	static private Location getLastBestLocation(LocationManager mLocationManager,Activity context) {

		if (!isAvailablePermissions (context))
			return null;

		@SuppressLint("MissingPermission")
		Location locationGPS = mLocationManager.getLastKnownLocation (LocationManager.GPS_PROVIDER);
        @SuppressLint("MissingPermission")
		Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }
    }
    public static boolean isAvailablePermissions(Activity context){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			if (context!=null && context.checkSelfPermission (Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
					&& context.checkSelfPermission (Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				int tmp=permissionCode++;
				context.requestPermissions (new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
						,tmp);
				String[] permissions = new String[2];
				int[] accesses = new int[2];
				context.onRequestPermissionsResult (tmp, permissions, accesses);
				int granted=0;
				for (int i=0;i<2;i++)
					if (permissions[i]!=null && (permissions[i].equals (Manifest.permission.ACCESS_FINE_LOCATION)
							|| permissions[i].equals (Manifest.permission.ACCESS_COARSE_LOCATION)))
						granted+=accesses[i]; //0 or -1
					else
						granted--;
				if (granted<0) {
					Toast.makeText(context, "Нет прав на определение местоположения", 15);
					return false;
				}
			}
		return true;
	}
}
