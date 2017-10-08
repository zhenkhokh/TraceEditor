package ru.android.zheka.db;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ConcurrentModificationException;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.google.android.gms.maps.model.LatLng;

public class DbFunctions {
	public static final String DEFAULT_CONFIG_NAME = "defy";
	// see https://developers.google.com/maps/documentation/directions/intro#Restrictions
	public static final String AVOID_TOLLS = "tolls";
	public static final String AVOID_HIGHWAYS = "highways";
	public static final String AVOID_FERRIES = "ferries";
	public static final String AVOID_INDOR = "indoor";

	@Deprecated
	public static Trace getTraceByName(String name){
		Trace out = null;
		try{ out = new Select()
		.from(Trace.class)
		.orderBy("RANDOM()")
		.where("name=?",name)
		.executeSingle();
		}catch(NullPointerException e){
			e.printStackTrace();			
		}finally{
			return out;
		}
	}
	@Deprecated
	public static Point getPointByName(String name){
		Point out = null;
		try{ out = new Select()
		.from(Point.class)
		.orderBy("RANDOM()")
		.where("name=?",name)
		.executeSingle();
		}catch(NullPointerException e){
			e.printStackTrace();			
		}finally{
			return out;
		}
	}
	public static String getNamePointByData(LatLng point){
		Point out = null;
		try{ out = new Select()
		.from(Point.class)
		.orderBy("RANDOM()")
		.where("data=?",(String)new UtilePointSerializer().serialize(point))
		.executeSingle();
		}catch(NullPointerException e){
			e.printStackTrace();			
		}finally{
			if (out==null)
				return null;
			return out.name;
		}
	}
	public static Model getModelByName(String name,Class<? extends Model> cls){
		Model out = null;
		try{ out = new Select()
		.from(cls)
		.orderBy("RANDOM()")
		.where("name=?",name)
		.executeSingle();
		}catch(NullPointerException e){
			e.printStackTrace();			
		}finally{
			return out;
		}
	}
	private static List<Model> getModelsByName(String name,Class<? extends Model> cls){
		List<Model> out = null;
		try{ out = new Select()
		.from(cls)
		.orderBy("RANDOM()")
		.where("name=?",name)
		.execute();
		}catch(NullPointerException e){
			e.printStackTrace();			
		}finally{
			return out;
		}
	}
	public static Model getTableByModel(Model model){
		String className = model.getClass().getName();
		Model out = null;
		try{ out = new Select()
		.from(Trace.class)
		.where(className+" = ? ",model.getId())
		.executeSingle();
		}catch(NullPointerException e){
			e.printStackTrace();			
		}finally{
			return out;
		}
	}
	public static List<Model> getTablesByModel(Class<? extends Model> class_){
		List<Model> out = null;
		try{ out = new Select()
		.from(class_)
		.execute();
		}catch(NullPointerException e){
			e.printStackTrace();
		}finally{
			return out;
		}
	}
	synchronized public static Long add(Model table) throws IllegalAccessException, IllegalArgumentException, InstantiationException{
		//TODO recursive save calling for references
		Model sTable = table.getClass().newInstance();
		Field[] fields = table.getClass().getDeclaredFields();		
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.set(sTable, field.get(table)); 
		}
		String name=null;
		try {
			name = (String) table.getClass().getField("name").get(table);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		Class cls = table.getClass();		
		List<Model> tables = getModelsByName(name, cls);		
		/*while(name!=null && tables.size()>1 ){			
			delete(tables.get(0));
			tables = getModelsByName(name, cls);
		}*/
		if (table.getId()!=null)
			Model.delete(cls, table.getId());
		if (tables.size()>0 && tables.get(0).getId()!=table.getId())
			throw new ConcurrentModificationException();
		if (tables.size()>1)
			throw new ConcurrentModificationException("more then one db items with same name");

		return sTable.save();
	}
	public static void delete(Model table){
		table.delete();
	}
	public static boolean exsist(Model table){
		return table.getId()!=null;
	}
	public static boolean exsistPoint(String name){
		Point point = getPointByName(name);
		return point!=null?true:false;
	}
	public static boolean exsistTrace(String name){
		Trace trace = getTraceByName(name);
		return trace!=null?true:false;
	}
}
