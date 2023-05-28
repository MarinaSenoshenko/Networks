package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AllInformation {
    private List<Location> locations;
    private Location currentLocation;
    private HashMap<Сoordinates, Location> locationMap = new HashMap<>();;
    
    private final Map<Location, List<Near>> nearMap = new HashMap<>();
    private final Map<String, Near> nearIdMap = new HashMap<>();

    public List<Location> getLocations() {
        return locations;
    }
    
    public Location getCurrentLocation() {
        return currentLocation;
    }
    
    public Location getLocation(int index) {
        return locations.get(index);
    }
    
    public List<Near> getNear(Location location) {
        return nearMap.get(location);
    }
    
    public String getNearDescription(String xid) {
        return nearIdMap.get(xid).getDescription();
    }

    public void setLocationMap(List<Location> locations) {
        for (var location: locations) {
            locationMap.put(location.getCoordinates(), location);
        }
        this.locations = locations;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
    
    public void setNear(List<Near> places, Location location) {
        nearMap.put(location, places);
    }

    public void addNear(Сoordinates coordinates, List<Near> places) {
        nearMap.put(locationMap.get(coordinates), places);

        for (var place: places) {
            nearIdMap.put(place.getXid(), place);
        }
    }

    public void addWeather(Сoordinates coordinates, Weather weather) {
        locationMap.get(coordinates).setWeather(weather);
    }

    public void addDescription(String xid, String description) {
        nearIdMap.get(xid).setDescription(description);
    }
    
    public boolean isLocationContainsNear(Location location) {
        return nearMap.containsKey(location);
    }
}
