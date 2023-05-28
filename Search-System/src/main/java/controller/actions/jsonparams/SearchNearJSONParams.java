package controller.actions.jsonparams;

public class SearchNearJSONParams extends ActionJSONParams {
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
}
