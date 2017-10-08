package ru.android.zheka.route;

/**
 * Async Task to access the Google Direction API and return the routing data
 * which is then parsed and converting to a route overlay
 *
 */

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.gmapexample1.Application;
import ru.android.zheka.gmapexample1.PositionUtil;


public class Routing extends AsyncTask<LatLng, Void, Route> {
    protected ArrayList<RoutingListener> _aListeners;
    protected TravelMode _mTravelMode;

    public enum TravelMode {
        BIKING("biking"),
        DRIVING("driving"),
        WALKING("walking"),
        TRANSIT("transit");

        protected String _sValue;

        private TravelMode(String sValue) {
            this._sValue = sValue;
        }

        protected String getValue() {
            return _sValue;
        }
    }


    public Routing() {
        this._aListeners = new ArrayList<RoutingListener>();//(Class<Routing.TravelMode>)clTravel
        Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
        		, Config.class);
        TravelMode mTravelMode = Enum.valueOf(TravelMode.class
        		,config.travelMode);
        this._mTravelMode = mTravelMode;
    }

    public void registerListener(RoutingListener mListener) {
        _aListeners.add(mListener);
    }

    protected void dispatchOnStart() {
        for (RoutingListener mListener : _aListeners) {
            mListener.onRoutingStart();
        }
    }

    protected void dispatchOnFailure() {
        for (RoutingListener mListener : _aListeners) {
            mListener.onRoutingFailure();
        }
    }

    protected void dispatchOnSuccess(PolylineOptions mOptions) {
        for (RoutingListener mListener : _aListeners) {
            mListener.onRoutingSuccess(mOptions);
        }
    }

    /**
     * Performs the call to the google maps API to acquire routing data and
     * deserializes it to a format the map can display.
     *
     * @param aPoints
     * @return
     */
    @Override
    protected Route doInBackground(LatLng... aPoints) {
        for (LatLng mPoint : aPoints) {
            if (mPoint == null) return null;
        }

        return new GoogleParser(constructURL(aPoints)).parse();
    }

    protected String constructURL(LatLng... points) {
        LatLng start = points[0];
        LatLng dest = points[1];
        String sJsonURL = "http://maps.googleapis.com/maps/api/directions/json?";

        final StringBuffer mBuf = new StringBuffer(sJsonURL);
        mBuf.append("origin=");
        mBuf.append(start.latitude);
        mBuf.append(',');
        mBuf.append(start.longitude);
        mBuf.append("&destination=");
        mBuf.append(dest.latitude);
        mBuf.append(',');
        mBuf.append(dest.longitude);
        mBuf.append("&sensor=true&mode=");
        mBuf.append(_mTravelMode.getValue());
        if(points.length>2){
        	mBuf.append("&waypoints=optimize:")
        	.append(ru.android.zheka.gmapexample1.Application.config.optimization);
        	for (int i=2;i<points.length;i++)
        		mBuf.append("|"+points[i].latitude+","+points[i].longitude);        	
        }
        
        Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
        		, Config.class);
        String avoid  = config.avoid;
        if (!avoid.isEmpty())
        	mBuf.append("&avoid="+avoid);
        
        return mBuf.toString();
    }

    @Override
    protected void onPreExecute() {
        dispatchOnStart();
    }

    @Override
    protected void onPostExecute(Route result) {
        if (result == null) {
            dispatchOnFailure();
        } else {
            PolylineOptions mOptions = new PolylineOptions();

            for (LatLng point : result.getPoints()) {
                mOptions.add(point);
            }

            dispatchOnSuccess(mOptions);
        }
    }//end onPostExecute method
}
