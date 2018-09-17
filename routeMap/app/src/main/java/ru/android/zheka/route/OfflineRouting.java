package ru.android.zheka.route;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

public class OfflineRouting extends Routing {
    LatLng[] aPoints;
    public OfflineRouting(LatLng ... aPoints){
        this.aPoints = aPoints;
    }
    //ignore input
    @Override
    public Route doInBackground(final LatLng... aPoints) {
        Route route = new Route ();
        route.addPoints (Arrays.asList (this.aPoints));
        int len = (int) (BellmannFord.getPathLength(this.aPoints)*1000.0);//TODO get from db
        route.setLength (len);
        Segment segment = new Segment ();
        segment.setLength (len);
        route.addSegment (segment);
        return route;
    }
}
