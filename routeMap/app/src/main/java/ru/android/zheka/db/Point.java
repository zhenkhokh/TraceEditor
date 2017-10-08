package ru.android.zheka.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.google.android.gms.maps.model.LatLng;

public class Point extends Model{
	@Column(name = "name")
	public String name;
	@Column(name = "data")
	public LatLng data;
	public String toString(){
		UtilePointSerializer util = new UtilePointSerializer();
		return new StringBuilder().append("name:")
				.append(name)
				.append(" geo:")
				.append((String)util.serialize(data)).toString();
	}
}
