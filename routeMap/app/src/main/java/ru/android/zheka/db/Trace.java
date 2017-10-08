package ru.android.zheka.db;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class Trace extends Model{
	@Column(name = "name")
	public String name;
	@Column(name = "start")
	public LatLng start;
	@Column(name = "end")
	public LatLng end;
	//use , for lat-lng delimiter and ; for points delimiter
	@Column(name = "data")
	public DataTrace data;
	//public PolylineOptions data;
	public String toString(){
		UtilePointSerializer util = new UtilePointSerializer();
		UtileTracePointsSerializer util1 = new UtileTracePointsSerializer();
		return new StringBuilder().append("name:")
				.append(name)
				.append(" start:")
				.append((String)util.serialize(start))
				.append(" end:")
				.append((String)util.serialize(end))
				.append("data:")
				.append((String)util1.serialize(data)).toString();
	}
}
