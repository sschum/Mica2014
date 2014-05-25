package de.tarent.mica.net;

import java.lang.reflect.Field;

public class Utils {

	public static Object getPrivate(String fieldName, Object object) throws Exception{
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		try{
			return field.get(object);
		}finally{
			field.setAccessible(false);
		}
	}
	
	public static void setPrivate(String fieldName, Object object, Object toSet) throws Exception{
		Field field = object.getClass().getDeclaredField(fieldName);
		field.setAccessible(true);
		try{
			field.set(object, toSet);
		}finally{
			field.setAccessible(false);
		}
	}
}
