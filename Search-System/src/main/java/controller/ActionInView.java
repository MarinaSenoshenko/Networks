package controller;

public class ActionInView {
    private String actionName;
    private String actionParam;
    
    public ActionInView(String actionName, String actionParam) {
    	this.actionName = actionName;
    	this.actionParam = actionParam;
    }
    
    public String getName() {
    	return actionName;
    }
    
    public String getParam() {
    	return actionParam;
    }
}
