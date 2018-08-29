package ru.android.zheka.gmapexample1;

import com.google.android.gms.maps.GoogleMap;

public class MapTypeHandler {
    static public int userCode = GoogleMap.MAP_TYPE_NORMAL;// do not use NONE
    static public enum Type{
        NONE(String.valueOf (GoogleMap.MAP_TYPE_NONE)),
        NORMAL(String.valueOf (GoogleMap.MAP_TYPE_NORMAL)),
        SATELLITE(String.valueOf (GoogleMap.MAP_TYPE_SATELLITE)),
        TERRAIN(String.valueOf (GoogleMap.MAP_TYPE_TERRAIN)),
        HYBRID(String.valueOf (GoogleMap.MAP_TYPE_HYBRID));

        private String code;
        Type(String code) {this.code = code;}

        private String getCode() {
            return code;
        }
    }
    Type type;
    public MapTypeHandler(int code){
        //type = Enum.valueOf (Type.class,String.valueOf (code));
        switch (code){
            case GoogleMap.MAP_TYPE_NONE:{
                type = Type.NONE;
                break;
            }
            case GoogleMap.MAP_TYPE_NORMAL:{
                type = Type.NORMAL;
                break;
            }
            case GoogleMap.MAP_TYPE_SATELLITE:{
                type = Type.SATELLITE;
                break;
            }
            case GoogleMap.MAP_TYPE_TERRAIN:{
                type = Type.TERRAIN;
                break;
            }
            case GoogleMap.MAP_TYPE_HYBRID:{
                type = Type.HYBRID;
                break;
            }
            default:{ throw new IllegalStateException("undefined type of map");}
        }
    }

    public int getCode() {
        return Integer.valueOf ( type.getCode ());
    }

    public Type getType() {
        return type;
    }
}
