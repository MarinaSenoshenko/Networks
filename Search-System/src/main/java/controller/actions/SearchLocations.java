package controller.actions;

import controller.actions.jsonparams.ActionJSONParams;
import controller.actions.jsonparams.SearchLocationsJSONParams;
import main.Main;
import model.AllInformation;
import model.Location;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;
import view.SearchView;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.*;


public class SearchLocations extends AbstractRequest {
    private final String locationName;

    public SearchLocations(ActionArgs args) {
        super(args.httpClient());
        this.locationName = args.params();
    }

    private List<Location> getLocations(HttpResponse<String> response, String resultsKeyName) {   	
    	ArrayList<Location> res = new ArrayList<Location>();      
    	JSONObject jsonObject = new JSONObject(response.body());
        JSONArray responseLocations = jsonObject.getJSONArray(resultsKeyName);
        
        for (var responseLocation : responseLocations) {
            try {
            	Location location = objectMapper.readValue(responseLocation.toString(), Location.class);
            	res.add(location);
            } catch (JSONException | JsonProcessingException ignored) {}
            
        }
        return res;
    }
    
    @Override
    protected URI buildURL(ActionJSONParams actionJSONParams) {
        try {
        	URIBuilder builder = new URIBuilder(super.buildURL(actionJSONParams));
        	   
            SearchLocationsJSONParams searchLocationsJSONParams = (SearchLocationsJSONParams)actionJSONParams;
            builder.addParameter(searchLocationsJSONParams.getLocationNameParam(), locationName);
        	return builder.build();
        } catch (URISyntaxException e) {
        	Main.log.error(getClass() + ": Seatch Location: " + e.getMessage());
        	return null;
        }
    }
    
    @Override
    protected void getAnswerFromResponse(HttpResponse<String> response, AllInformation allInformation, 
    		SearchView view, ActionJSONParams actionJSONParams) {

        SearchLocationsJSONParams searchLocationsJSONParams = (SearchLocationsJSONParams)actionJSONParams;
        
        List<Location> locations = getLocations(response, searchLocationsJSONParams.getResultsKeyName());
        allInformation.setLocationMap(locations);    

        try {
            view.showLocations(locations);
        } catch (Exception e) {
        	Main.log.error(getClass() + ": Can't update view: " + e.getMessage());
        }
    }
    
    @Override
    protected Class<? extends ActionJSONParams> getJSONClass() {
        return SearchLocationsJSONParams.class;
    }
}
