package ru.android.zheka.geo;

public abstract class GeoParser {
    String region,city,street,house;
    public abstract GeoCoder parse();
    public GeoParser(String region,String city,String street, String house){
        this.region = region;
        this.city = city;
        this.street = street;
        this.house = house;
    }
}
