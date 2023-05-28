package controller.actions.jsonparams;

public class SearchNearDescriptionJSONParams extends ActionJSONParams {
    private String infoKeyName;
    private String descriptionKeyName;

    public String getInfoKeyName() {
        return infoKeyName;
    }
    
    public String getDescriptionKeyName() {
        return descriptionKeyName;
    }

    public void setInfoKeyName(String infoKeyName) {
        this.infoKeyName = infoKeyName;
    }

    public void setDescriptionKeyName(String descriptionKeyName) {
        this.descriptionKeyName = descriptionKeyName;
    }
}