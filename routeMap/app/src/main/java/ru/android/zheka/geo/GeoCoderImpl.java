package ru.android.zheka.geo;

import android.util.Xml;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import ru.android.zheka.route.XMLParser;

import static java.util.Locale.getDefault;

public class GeoCoderImpl extends XMLParser implements GeoCoder {
    String[] addresses;
    LatLng [] points;

    public GeoCoderImpl(String feedUrl) throws JSONException {
        super (feedUrl);
        InputStream is = getInputStream ();
        Scanner scanner = new Scanner (is);
        StringBuilder sb = new StringBuilder ();
        while (scanner.hasNext ())
            sb.append (scanner.next ()).append(" ");
        System.out.println(sb.toString());
        //JSONObject json = new JSONObject (new String(sb.toString ().getBytes(Charset.forName ("UTF-8"))));
        JSONObject json = new JSONObject (sb.toString ());
        JSONObject  response = json.getJSONObject ("response");
        JSONObject geoObjectCollection = response.getJSONObject("GeoObjectCollection");
        JSONArray featureMember = geoObjectCollection.getJSONArray("featureMember");
        int n = featureMember.length ();
        int index = 0;
        while (scanner.hasNext ())
            sb.append (scanner.next ()).append (" ");
        /*XmlPullParser xml = Xml.newPullParser ();
        try {
            xml.setInput (new BufferedReader(new InputStreamReader (is)));
            xml.re

        } catch (XmlPullParserException e) {
            e.printStackTrace ();
        }
        */
        List addresses = new ArrayList<String> ();
        List points = new ArrayList<LatLng> ();

        while(index<n){
            if (!featureMember.isNull (index++) ){
                LatLng point;
                    try {
                        JSONObject element = featureMember.getJSONObject (index-1);
                        JSONObject geoObject = element.getJSONObject ("GeoObject");
                        JSONObject geocoderMetaData = geoObject.getJSONObject ("metaDataProperty")
                                .getJSONObject ("GeocoderMetaData");
                        String status = geocoderMetaData.get ("precision").toString ();
                        String name = (String)geoObject.get ("name");
                        sb = new StringBuilder ();
                        sb.append (geoObject.get ("description").toString())
                                .append (", ")
                                .append (name)
                                .append (". Точность: ")
                                .append (status);
                        String sPoint = geoObject.getJSONObject ("Point").get ("pos").toString ();
                        String[] lonLat = sPoint.split (" ");
                        point = new LatLng (Double.valueOf (lonLat[1]), Double.valueOf (lonLat[0]));
                }catch (JSONException |NumberFormatException e){
                        e.printStackTrace ();
                        continue;
                }
                addresses.add (sb.toString ());
                points.add ( point);
            }
        }
        this.addresses = (String[]) addresses.toArray (new String[1]);
        this.points = (LatLng[]) points.toArray (new LatLng[1]);
    }

    @Override
    public String[] getAdresses() {
        /*String [] addresses = {"Северо-Западный федеральный округ+Санкт-Петербург+Льва+Толстого+16"
                ,"Москва+Москва+Льва+Толстого+16"};
                */
        return addresses;
    }

    @Override
    public LatLng[] getPoints() {
        /*LatLng l1 = new LatLng (59.96448, 30.326468 );
        LatLng l2 = new LatLng (55.733842,37.588144 );
        LatLng [] points = {l1,l2};
        */
        return points;
    }

    @Override
    public int getKey(String address) {
        //String [] addresses = getAdresses ();
        for (int i=0;i<addresses.length;i++){
            if (addresses[i].equals (address))
                return i;
        }
        return -1;
    }
}
