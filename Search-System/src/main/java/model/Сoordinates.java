package model;

public class Сoordinates {
    private double lat;
    private double lng;

    public Сoordinates(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public Сoordinates() {}

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
    
    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
