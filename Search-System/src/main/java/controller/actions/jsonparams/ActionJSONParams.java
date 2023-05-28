package controller.actions.jsonparams;

import java.util.Map;

public class ActionJSONParams {
    private String URL;
    private Map<String, String> args;

    public String getURL() {
        return URL;
    }
    
    public Map<String, String> getArgs() {
        return args;
    }

    public void setBaseURL(String baseURL) {
        this.URL = baseURL;
    }

    public void setParams(Map<String, String> args) {
        this.args = args;
    }
}
