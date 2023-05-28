package controller.actions;

import model.*;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import controller.actions.jsonparams.*;
import main.Main;
import view.SearchView;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public class SearchNearDescription extends AbstractRequest {
    private final String xid;

    public SearchNearDescription(HttpClient client, String xid) {
        super(client);
        this.xid = xid;
    }

    @Override
    protected void getAnswerFromResponse(HttpResponse<String> response, AllInformation allInformation, 
    		SearchView searchView, ActionJSONParams actionJSONParams) {
        SearchNearDescriptionJSONParams searchNearDescriptionJSONParams = 
        		(SearchNearDescriptionJSONParams)actionJSONParams;
        JSONObject object = new JSONObject(response.body());
 
        try {
            JSONObject nearInfo = object.getJSONObject(searchNearDescriptionJSONParams.getInfoKeyName());
            String nearDescription = (String)nearInfo.get(searchNearDescriptionJSONParams.getDescriptionKeyName());
            allInformation.addDescription(xid, nearDescription);
            searchView.setActive(xid);
            
        } catch (JSONException ignored) {}
    }

    @Override
    protected URI buildURL(ActionJSONParams actionJSONParams) {
    	URIBuilder builder = new URIBuilder(super.buildURL(actionJSONParams));
        builder.setPath(builder.getPath() + xid);

        try {
            return builder.build();
        } catch (URISyntaxException e) {
        	Main.log.error(getClass() + ": " + e.getMessage());
            return null;
        }
    }
    
    @Override
    protected Class<? extends ActionJSONParams> getJSONClass() {
        return SearchNearDescriptionJSONParams.class;
    }
}