package main;

import controller.Controller;
import controller.actions.Action;
import model.AllInformation;
import view.SearchView;

import java.io.IOException;

public class App {
    private final Controller controller;
    private final AllInformation allInformation;
    private final SearchView searchView;

    public App() throws IOException {
        this.searchView = new SearchView();
        this.controller = new Controller(searchView);
        this.allInformation = new AllInformation();
    }

    public void startApp() {
    	Main.log.info("Starting app...");
        while (true) {
            Action action = null;
            try {
                while (action == null) {     
                    action = controller.getAction();
                }
                action.run(allInformation, searchView);
            } catch (Exception ignored) {}
        }
    }
}
