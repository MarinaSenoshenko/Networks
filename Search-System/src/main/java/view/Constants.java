package view;

import java.awt.*;

public class Constants {
	public static final Color BACKGROUND = new Color(150, 255, 255);
    public static final Color PANEL = new Color(10, 200, 255);
    public static final Color SEARCH = new Color(255, 255, 255);
    public static final Color ELEMENT = new Color(50, 255, 255);
    
    public final static Size APP_FRAME = new Size(900, 800);
    public final static Size APP_LOCATION = new Size(250, 10);
    public final static Size APP_LOCATION_LIST = new Size(230, 65);
    
    public final static Size MAX_SEARCH = new Size(5000, 25);
    public final static Size MAX_PLACE = new Size(5000, 30);
    
    public final static int APP_DESCRIPTION = 50;
    
    static class Size {
    	private int width;
    	private int height;
    	
    	private Size(int width, int height) {
    		this.width = width;
    		this.height = height;
    	}
    	
    	public int getWidth() {
    		return width;
    	}
    	
    	public int getHeight() {
    		return height;
    	}
    }
} 