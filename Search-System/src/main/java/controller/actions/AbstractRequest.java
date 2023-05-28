package controller.actions;

import controller.actions.jsonparams.ActionJSONParams;
import main.Main;
import model.AllInformation;

import org.apache.http.client.utils.URIBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import view.SearchView;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public abstract class AbstractRequest implements Action {
    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper = new ObjectMapper();
    
    private static final int RESPONSE_CODE = 200;

    public AbstractRequest(HttpClient httpClient) {
        this.httpClient = httpClient;
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public final void run(AllInformation allInformation, SearchView searchView) {   
        try {
            ActionJSONParams actionJSONParams = getJSON().get();
            HttpRequest request = HttpRequest.newBuilder().uri(buildURL(actionJSONParams)).build();
        	HttpResponse<String>response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());  
        	
        	if (response.statusCode() == RESPONSE_CODE) {
        		getAnswerFromResponse(response, allInformation, searchView, actionJSONParams);
            }
        } catch (Exception exc) {
        	Main.log.error(getClass() + ": " + exc.getMessage());
        }
    }

    protected URI buildURL(ActionJSONParams actionJSONParams) {
        try {
        	URIBuilder uriBuilder = new URIBuilder(actionJSONParams.getURL());
        
            for (var item : actionJSONParams.getArgs().entrySet()) {
                uriBuilder.addParameter(item.getKey(), item.getValue());
            }
            return uriBuilder.build();
        } catch (URISyntaxException exc) {
        	Main.log.error(getClass() + ": " + exc.getMessage());
        	return null;
        }
    }
   
    private Optional<? extends ActionJSONParams> getJSON() {
       try {
       	  return Optional.of(objectMapper.readValue(AbstractRequest.class.getResourceAsStream("/" + 
                 this.getClass().getSimpleName() + ".json"), getJSONClass()));
         } catch (IOException exc) {
    	    Main.log.error(getClass() + ": " + exc.getMessage());
    	    return null;
       }
    }
   
    protected abstract Class<? extends ActionJSONParams> getJSONClass();

    protected abstract void getAnswerFromResponse(HttpResponse<String> response, AllInformation data, 
    		SearchView searchView, ActionJSONParams actionParams);
}
