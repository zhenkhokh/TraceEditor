package ru.android.zheka.db;

import java.util.ArrayList;

import com.activeandroid.serializer.TypeSerializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class UtileTracePointsSerializer extends TypeSerializer{
	public static final String tDelimiter = ";";
	public static final String eDelimiter = "_";
	DataTrace dataTrace;

	@Override
	public Class<?> getDeserializedType() {
		return DataTrace.class;
		//return PolylineOptions.class;
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
        dataTrace = (DataTrace)data;
        StringBuilder sb = new StringBuilder();
        UtilePointSerializer utilePointSerializer = new UtilePointSerializer();
        for (LatLng point : dataTrace.getAllPoints ().getPoints()) {
			sb.append(utilePointSerializer.serialize(point))
				.append(tDelimiter);
		}
		if (sb.length ()>=1)//?
        	sb.deleteCharAt(sb.length()-1);
        sb.append(eDelimiter);
        
        for (String point : dataTrace.extraPoints) {
			sb.append(point)
				.append(tDelimiter);
		}
		if (sb.length ()>=1)//?
        	sb.deleteCharAt(sb.length()-1);
        
		return sb.toString();
	}

	@Override
	public Object deserialize(Object data) {
        if (data == null) {
            return null;
        }
		String in  = (String)data;
		String[] allAndExtra = in.split(eDelimiter);
		String[] points = allAndExtra[0].split(tDelimiter);
		String[] ePoints = allAndExtra[1].split(tDelimiter);
		//PolylineOptions out = new PolylineOptions();
		DataTrace out = new DataTrace();
		for (int i = 0; i < points.length; i++) {
			String[] s = points[i].split(UtilePointSerializer.pDelimiter);
			Double latitude = new Double(s[0]);
			Double longitude = new Double(s[1]);
			out.getAllPoints ().add(new LatLng(latitude, longitude));
		}
		for (int i = 0; i < ePoints.length; i++) {
			out.extraPoints.add(ePoints[i]);
		}
		return out;
	}
}

