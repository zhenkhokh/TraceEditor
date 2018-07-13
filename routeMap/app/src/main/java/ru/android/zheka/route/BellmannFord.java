package ru.android.zheka.route;

import com.google.android.gms.maps.model.LatLng;

public class BellmannFord {
    final static double toRad = Math.PI/180;
    final static double R = 6371;//km
    // !!! use process once
    public static double length = 0;

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

    private static double getPathLength(LatLng[] latLngs){
        int n = latLngs.length;
        double pathLen = 0;
        double longitude = latLngs[0].longitude * toRad;
        double latitude = latLngs[0].latitude * toRad;
        for (int j = 1; j < n; j++) {
            double latitude1 = latLngs[j].latitude * toRad;
            double longitude1 = latLngs[j].longitude * toRad;
            double dLambda = -(longitude1 - longitude);
            double dLat = latitude1 - latitude;
            double cosArg = Math.cos (dLat) + (Math.cos (dLambda) - 1.0)
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
}
