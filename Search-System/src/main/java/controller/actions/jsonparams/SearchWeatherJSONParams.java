package controller.actions.jsonparams;


public class SearchWeatherJSONParams extends ActionJSONParams {
    private String weatherKeyName;
    private String latKeyName;
    private String lngKeyName;

    public String getLatKeyName() {
        return latKeyName;
    }
    
    public String getLngKeyName() {
        return lngKeyName;
    }

   public void setLatKeyName(String latKey) {
        latKeyName = latKey;
    }

    public void setLngKeyName(String lngKey) {
        lngKeyName= lngKey;
    }

    public String getWeatherKeyName() {
        return weatherKeyName;
    }

    public void setWeatherKeyName(String weatherKeyName) {
        this.weatherKeyName = weatherKeyName;
    }
    
}
