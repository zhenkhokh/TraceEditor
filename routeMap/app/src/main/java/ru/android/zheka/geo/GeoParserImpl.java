package ru.android.zheka.geo;

import org.json.JSONException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class GeoParserImpl extends GeoParser {
    static String encode = "UTF-8";
    public GeoParserImpl(String region, String city, String street, String house) {
        super (region, city, street, house);
    }

    @Override
    public GeoCoder parse() {
        String url = "https://geocode-maps.yandex.ru/1.x/?geocode=";
        StringBuilder sb = new StringBuilder ();
        sb.append (url);
        try {
            URLEncoder.encode ("привет","UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace ();
        }
        try {
            if (!region.equals ("-"))
                sb.append (URLEncoder.encode(region,encode)).append ("+");
            if (!city.equals ("-"))
                sb.append (URLEncoder.encode(city,encode)).append ("+");
            if (!street.equals ("-"))
                sb.append (URLEncoder.encode(street,encode)).append ("+");
            if (!house.equals ("-"))
                sb.append (URLEncoder.encode(house,encode));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace ();
        }
        sb.append ("&format=json");
        GeoCoder out = null;
        try {
            out = new GeoCoderImpl (/*URLEncoder.encode(sb.toString (),encode)*/sb.toString ());
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        return out;
    }
}
