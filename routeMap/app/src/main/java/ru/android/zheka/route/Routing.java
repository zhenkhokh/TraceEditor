package ru.android.zheka.route;

/**
 * Async Task to access the Google Direction API and return the routing data
 * which is then parsed and converting to a route overlay
 *
 */

import android.app.Service;
import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.io.Resources;

import java.util.ArrayList;

import ru.android.zheka.db.Config;
import ru.android.zheka.db.DbFunctions;
import ru.android.zheka.gmapexample1.Application;
import ru.android.zheka.gmapexample1.PositionUtil;
import ru.android.zheka.gmapexample1.R;

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

    protected void dispatchOnSuccess(Route route) {
        for (RoutingListener mListener : _aListeners) {
            mListener.onRoutingSuccess(route);
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
        Config config = (Config) DbFunctions.getModelByName(DbFunctions.DEFAULT_CONFIG_NAME
                , Config.class);
        if(points.length>2){
            System.out.println ("optimizationBellmanFlag= "+Application.optimizationBellmanFlag
                    +" points.length="+points.length
                    +" config.bellmanFord="+config.bellmanFord);
            mBuf.append ("&waypoints=optimize:")
                    .append (config.optimization);
            boolean isBellman = config.bellmanFord.equals ( Application.optimizationBellmanFlag);
/*            if (isBellman) {
                points = BellmannFord.process(points);
            }
*/
//TODO make post request, not limit by 8 points in get-request
            for (int i=2;i<points.length;i++) {
                if ((isBellman && i<=4) || !isBellman)
                    mBuf.append ("|" + points[i].latitude + "," + points[i].longitude);
            }
        }
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

            /*PolylineOptions mOptions = new PolylineOptions();

            for (LatLng point : result.getPoints()) {
                mOptions.add(point);
            }
*/
            dispatchOnSuccess(result);
        }
    }//end onPostExecute method
}
