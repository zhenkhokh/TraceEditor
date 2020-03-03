package ru.android.zheka.gmapexample1.edit;

import com.activeandroid.Model;

import java.lang.reflect.Field;

public class Editable{
	private String name;
	public Model model;
	public Editable(Model model){
		this.model = model;
		try{name = (String) getField().get(model);
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		try{getField().set(model, name);
		}catch(IllegalAccessException e){
			e.printStackTrace();
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		this.name = name;
	}
	private Field getField(){
		Field field=null;
		try{field = model.getClass().getField("name");
		}catch(NoSuchFieldException e){
			e.printStackTrace();
		}catch (SecurityException e) {
			e.printStackTrace();
		}
		return field;
	}
}
