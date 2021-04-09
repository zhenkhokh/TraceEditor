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
    public GeoCoder parse(String key) {
        String url = "https://geocode-maps.yandex.ru/1.x/?apikey="+key+"&geocode=";
        StringBuilder sb = new StringBuilder ();
        sb.append (url);
        try {
            URLEncoder.encode ("привет","UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace ();
        }
        try {
            if (!region.isEmpty ())
                sb.append (URLEncoder.encode(region,encode)).append ("+");
            if (!city.isEmpty ())
                sb.append (URLEncoder.encode(city,encode)).append ("+");
            if (!street.isEmpty ())
                sb.append (URLEncoder.encode(street,encode)).append ("+");
            if (!house.isEmpty ())
                sb.append (URLEncoder.encode(house,encode));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace ();
        }
        sb.append ("&format=json");
        try {
            return new GeoCoderImpl (/*URLEncoder.encode(sb.toString (),encode)*/sb.toString ());
        } catch (JSONException e) {
            throw new YandexGeoCoderException ();
        } catch (Exception e){
            throw e;
        }
    }
    public class YandexGeoCoderException extends RuntimeException{}
}
