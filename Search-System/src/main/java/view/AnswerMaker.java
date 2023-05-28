package view;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import main.Main;
import model.Ignore;

public class AnswerMaker {
    private static Object getValue(String fieldName, Field field, Class<? extends Object> 
    objectClass, Object object) {    	
		try {
			Object value = objectClass.getDeclaredMethod("get" + 
		    fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1)).invoke(object);
			if (value == null) {
	    		return (Object)"no info";
	    	}
	    	return value;
		} catch (Exception exc) {
			 Main.log.error(AnswerMaker.class + ": " + exc.getMessage());
	         return null;
		}    	
    }
    
    public static Map<String, Object> getAsMap(Object object) {
        Class<? extends Object> objectClass = object.getClass();
        Map<String, Object> resMap = new HashMap<>();

        for (var field: objectClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Ignore.class)) {
            	String fieldName = field.getName();
            	resMap.put(fieldName, getValue(fieldName, field, objectClass, object));
            }
        }
        return resMap;
    }
}
