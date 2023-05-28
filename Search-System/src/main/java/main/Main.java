package main;

import org.apache.log4j.*; 

public class Main {	
	public static final Logger log = Logger.getLogger(Main.class);   
	
    public static void main(String[] args) {    
        try {
        	new App().startApp();
        } catch (Exception exc) {
        	log.error(Main.class + ": " + exc.getMessage());
        }
     }
}
