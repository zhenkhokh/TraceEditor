package ru.android.zheka.route;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Arrays;

import ru.android.zheka.gmapexample1.MapsActivity;

public class BellmannFord {
    final static double toRad = Math.PI/180;
    final static double R = 6371;//km
    // !!! use process once
    public static double length = 0;
    public static float bearing=-1000;
    static double longitude1;
    static double longitude;
    static double latitude1;
    static double latitude;
    static double dr;
    static double dr_tay;
    static private double eps = 1.0E3;

    public static LatLng[] process(LatLng[] latLngs){
        LatLng [] order = fliplr (latLngs);
        order = getOrder(order);
        order = fliplr (order);
        order = getOrder(order);
        length = getPathLength (order);
        System.out.println ("BellmanFord opt length = "+length);
        return order;
    }

    private static LatLng[] getOrder(LatLng[] latLngs) {
        int n = latLngs.length;
        LatLng [] reOrder = latLngs;
        boolean whileTrue = true;
        double optLen = getPathLength(reOrder);
        while (whileTrue){
            whileTrue = false;
            for (int i = 1; i < n-1; i++)// do not touch end point
                for (int j=i+1; j < n-1; j++){
                    LatLng tmp = reOrder[j];
                    reOrder[j] = reOrder[i];
                    reOrder[i] = tmp;
                    double curLen = getPathLength(reOrder);
                    if (curLen < optLen){
                        optLen = curLen;
                        whileTrue = true;
                        System.out.println ("new opt: optLen="+optLen);
                    }else{
                        tmp = reOrder[j];
                        reOrder[j] = reOrder[i];
                        reOrder[i] = tmp;
                    }
            }
        }
        return reOrder;
    }

    public static double getPathLength(LatLng[] latLngs){
        int n = latLngs.length;
        double pathLen = 0;
        if (n==0)
            return pathLen;
        double longitude = latLngs[0].longitude * toRad;
        double latitude = latLngs[0].latitude * toRad;
        for (int j = 1; j < n; j++) {
            double latitude1 = latLngs[j].latitude * toRad;
            double longitude1 = latLngs[j].longitude * toRad;
            double dLon = -(longitude1 - longitude);
            double dLamda = latitude1 - latitude;
            double cosArg = Math.cos (dLamda) + (Math.cos (dLon) - 1.0)
                    * Math.cos (latitude) * Math.cos (latitude1);
            latitude = latitude1;
            longitude = longitude1;
            double arg = Math.sqrt (1 - cosArg * cosArg) / cosArg;
            // atg Tailor for small arg
            arg = arg - 1.0 / 3 * arg * arg * arg;
            pathLen += R * Math.abs (arg);
        }
        return pathLen;
    }
    static private LatLng[] fliplr(LatLng[] points){
        int n = points.length;
        LatLng[] inv = new LatLng [n];
        for (int i = 0;i < n;i++){
            inv[n-i-1] = points[i];
        }
        return inv;
    }
    static public float getBearing(LatLng[] points) throws NoDirectionException, MissMatchDataException {
        if (points.length<2)
            throw new MissMatchDataException ();
        LatLng point = round (points[0]);
        LatLng point1 = round (points[1]);
        if (point.equals (point1)){
            int i=2;
            while(point.equals (point1) && i<points.length){
                point1 = round (points[i++]);
            }
            if (point.equals (point1))
                throw  new NoDirectionException ();
        }
        longitude1 = point1.longitude*toRad;
        longitude = point.longitude*toRad;
        latitude1 = point1.latitude*toRad;
        latitude = point.latitude*toRad;
        double dLon = longitude1 - longitude;
        double dLambda = latitude1 - latitude;
        double dr2 = 2.0*(1 + (1.0 - Math.cos (dLon))*Math.cos(latitude1)*Math.cos (latitude)
            - Math.cos (dLambda));
        dr = Math.sqrt (dr2);
        dr_tay = Math.sin(dLambda) - (1 - Math.cos (dLon))*Math.cos (latitude1)*Math.cos (latitude);
        //if (dr==0)
        //    throw  new NoDirectionException ();
        bearing = (float) (Math.acos (dr_tay/dr)/toRad); // 2 roots
        if (dLon<0)
            bearing = (float) 360.0 - bearing;
        System.out.println ("bearing="+bearing+" points="+ Arrays.asList (points));
        return bearing;
    }
    public static LatLng round(LatLng point){
        double lat = Math.round (point.latitude*eps)/eps;
        double lon = Math.round (point.longitude*eps)/eps;
        return new LatLng (lat,lon);
    }
    static public class MissMatchDataException extends Exception{

    }

    static public class NoDirectionException extends Exception{

    }
}


