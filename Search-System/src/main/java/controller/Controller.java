package controller;

import controller.actions.Action;
import controller.actions.ActionArgs;
import view.SearchView;
import java.net.http.HttpClient;

public class Controller {
    private final HttpClient httpClient;
    private final SearchView searchView;
    private final Factory factory = new Factory();

    public Controller(SearchView searchView) {
        this.searchView = searchView;
        this.httpClient = HttpClient.newHttpClient();
    }


    public Action getAction() {
        ActionInView actionInView = searchView.waitActionInView();
        
        return factory.createObject(actionInView.getName(), new ActionArgs(httpClient, actionInView.getParam())); 
    }
}
