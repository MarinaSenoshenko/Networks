package controller;

import controller.actions.Action;
import controller.actions.ActionArgs;
import main.Main;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class Factory {
    private final HashMap<String, String> actionNames = new HashMap<String, String>();
    
    public Factory() {
    	try (InputStream inputStream = Factory.class.getResourceAsStream("/actions.properties")) {
            Properties properties = new Properties();
            properties.load(inputStream);
       
            for (var propertyName : properties.stringPropertyNames()) {
        	    actionNames.put(propertyName, properties.getProperty(propertyName));
            }
        }
    	catch (IOException exc) {
    		Main.log.error(getClass() + ": " + exc.getMessage());
        }
    }
   

    public Action createObject(String commandName, ActionArgs args) {
        try {
    	    Class<?> actionClass = Class.forName("controller.actions." + actionNames.get(commandName));
            return (Action)actionClass.getConstructor(ActionArgs.class).newInstance(args);
        }
        catch (Exception exc) {
        	Main.log.error(getClass() + ": " + exc.getMessage());
        	return null;
        }
    }
}
