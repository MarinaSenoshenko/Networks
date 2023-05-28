package controller.actions;

import controller.actions.jsonparams.ActionJSONParams;
import controller.actions.jsonparams.SearchNearJSONParams;
import main.Main;
import model.AllInformation;
import model.Near;
import model.Сoordinates;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;

import com.fasterxml.jackson.core.JsonProcessingException;

import view.SearchView;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SearchNear extends AbstractRequest {
	private Сoordinates coordinates;
	
	public SearchNear(HttpClient client, Сoordinates coordinates) {
        super(client);
        this.coordinates = coordinates;
    }

    @Override
    protected URI buildURL(ActionJSONParams actionJSONParams) {
        try {
        	URIBuilder uriBuilder =  new URIBuilder(super.buildURL(actionJSONParams));
        	SearchNearJSONParams searchNearJSONParams = (SearchNearJSONParams)actionJSONParams;
            uriBuilder.addParameter(searchNearJSONParams.getLatKeyName(), String.valueOf(coordinates.getLat()));
            uriBuilder.addParameter(searchNearJSONParams.getLngKeyName(), String.valueOf(coordinates.getLng()));
            return uriBuilder.build();
        } catch (URISyntaxException exc) {
        	Main.log.error(getClass() + ": " + exc.getMessage());
        	return null;
        }
    } 

    @Override
    protected void getAnswerFromResponse(HttpResponse<String> response, AllInformation allInformation, 
    		SearchView view, ActionJSONParams actionJSONParams) {	
    	JSONArray responseList = new JSONArray(response.body());
        List<Near> nearList = new ArrayList<>();        

        for (var placesResponse : responseList) {
        	try {
                Near near = objectMapper.readValue(placesResponse.toString(), Near.class);
                if (near.getName() != "") {
                    nearList.add(near);
                    SearchNearDescription searchNearDescription = 
                    		new SearchNearDescription(httpClient, near.getXid());                  
                    Runnable searchNearDescriptionRunnable = () -> {
                        try {
                            searchNearDescription.run(allInformation, view);
                        } catch (Exception ignored) {}
                    };
                    CompletableFuture.runAsync(searchNearDescriptionRunnable);
                    
                }
            } catch (JsonProcessingException ignored) {}
        }
        
        allInformation.addNear(coordinates, nearList);
        view.showNear(nearList);
    }
    
    @Override
    protected Class<? extends ActionJSONParams> getJSONClass() {
        return SearchNearJSONParams.class;
    }  
}
