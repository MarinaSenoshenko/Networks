package controller.actions;

import model.AllInformation;
import model.Location;
import model.Сoordinates;
import view.SearchView;
import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;
import main.Main;

public class ShowNearAndWeather implements Action {
   private HttpClient httpClient;
   private String params;

    public ShowNearAndWeather(ActionArgs args) {
        this.httpClient = args.httpClient();
        this.params = args.params();
    }
    
    private void showNear(Location location, Сoordinates coordinates, AllInformation allInformation, 
    		SearchView searchView) {
    	SearchNear searchNear = new SearchNear(httpClient, coordinates);
    	
    	if (!allInformation.isLocationContainsNear(location)) {
            Runnable runSearchNear = () -> {
                try {
                    searchNear.run(allInformation, searchView);
                } catch (Exception ignored) {}
            };
            CompletableFuture.runAsync(runSearchNear);
        } else {
        	searchView.showNear(allInformation.getNear(location));
            
        }
    }
    
    private void showWheather(Location location, Сoordinates coordinates, AllInformation allInformation, 
    		SearchView searchView) {
    	SearchWeather searchWeather = new SearchWeather(httpClient, coordinates);
    	if (location.getWeather() == null) {
            Runnable runWeather = () -> {
                try {
                    searchWeather.run(allInformation, searchView);
                } catch (Exception ignored) {}
            };
            CompletableFuture.runAsync(runWeather);
        } else {
        	searchView.showWeather(location.getWeather());          
        }
    }

    @Override
    public void run(AllInformation allInformation, SearchView searchView) {
        int index = 0;
        try {
            index = Integer.parseInt(params);
        } catch (NumberFormatException e) {
        	Main.log.error("Wrong args: " + e.getMessage());
        }
        Location location = allInformation.getLocation(index);      
        allInformation.setCurrentLocation(location);        
        Сoordinates point = location.getCoordinates(); 
        
        showWheather(location, point, allInformation, searchView);
        showNear(location, point, allInformation, searchView);
       
    }
}
