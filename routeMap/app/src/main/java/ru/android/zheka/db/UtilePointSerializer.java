package ru.android.zheka.db;

import com.activeandroid.serializer.TypeSerializer;
import com.google.android.gms.maps.model.LatLng;

public class UtilePointSerializer extends TypeSerializer{
	static public final String pDelimiter = ","; 
	@Override
	public Class<?> getDeserializedType() {
		return LatLng.class;
	}
	
	@Override
	public Class<?> getSerializedType() {
		return String.class;
	}
	
	@Override
	public Object serialize(Object data) {
        if (data == null) {
            return null;
        }
		Double latitude = ((LatLng)data).latitude;
		Double longitude = ((LatLng)data).longitude;
		StringBuilder out = new StringBuilder();
		out.append(latitude.toString()).append(pDelimiter).append(longitude.toString());		
		return out.toString();
	}

	@Override
	public Object deserialize(Object data) {
        if (data == null) {
            return null;
        }
		String in  = (String)data;
		String[] s = in.split(pDelimiter);
		Double latitude = new Double(s[0]);
		Double longitude = new Double(s[1]);
		return new LatLng(latitude, longitude);
	}
}
