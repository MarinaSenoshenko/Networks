package controller.actions.jsonparams;

public class SearchLocationsJSONParams extends ActionJSONParams {
    private String locationNameParam;
    private String resultsKeyName;

    public String getLocationNameParam() {
        return locationNameParam;
    }
    
    public String getResultsKeyName() {
        return resultsKeyName;
    }

    public void setLocationNameParam(String locationNameParam) {
        this.locationNameParam = locationNameParam;
    }

    public void setResultsKeyName(String resultsKeyName) {
        this.resultsKeyName = resultsKeyName;
    }
}
