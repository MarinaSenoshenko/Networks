package controller.actions;

import controller.actions.jsonparams.ActionJSONParams;
import controller.actions.jsonparams.SearchWeatherJSONParams;
import main.Main;
import model.AllInformation;
import model.Weather;
import model.Сoordinates;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;

import view.SearchView;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public class SearchWeather extends AbstractRequest {
	private Сoordinates coordinates;
	
	public SearchWeather(HttpClient httpClient, Сoordinates coordinates) {
        super(httpClient);
        this.coordinates = coordinates;
    }

    private Weather getWeather(HttpResponse<String> response, String weatherKeyName) {
        JSONObject object = new JSONObject(response.body());
        JSONObject weatherResponse = object.getJSONObject(weatherKeyName);

        try {
            return objectMapper.readValue(weatherResponse.toString(), Weather.class);
        } catch (JsonProcessingException exc) {
        	Main.log.error(getClass() + ": " + exc.getMessage());
        	return null;
        }
    }
    
    @Override
    protected Class<? extends ActionJSONParams> getJSONClass() {
        return SearchWeatherJSONParams.class;
    }
    
    @Override
    protected URI buildURL(ActionJSONParams actionJSONParams) {
        try {
        	URIBuilder uriBuilder =  new URIBuilder(super.buildURL(actionJSONParams));
        	SearchWeatherJSONParams searchWeatherJSONParams = (SearchWeatherJSONParams)actionJSONParams;
            uriBuilder.addParameter(searchWeatherJSONParams.getLatKeyName(), String.valueOf(coordinates.getLat()));
            uriBuilder.addParameter(searchWeatherJSONParams.getLngKeyName(), String.valueOf(coordinates.getLng()));
            return uriBuilder.build();
        } catch (URISyntaxException exc) {
        	Main.log.error(getClass() + ": " + exc.getMessage());
        	return null;
        }
    }

    @Override
    protected void getAnswerFromResponse(HttpResponse<String> response, AllInformation allInformation, 
    		SearchView searchView, ActionJSONParams actionJSONParams) {
        SearchWeatherJSONParams searchWeatherJSONParams = (SearchWeatherJSONParams)actionJSONParams;
        String weatherKeyName = searchWeatherJSONParams.getWeatherKeyName();
        Weather weather = getWeather(response, weatherKeyName);
        
        allInformation.addWeather(coordinates, weather);
        searchView.showWeather(weather);      
    }
}
