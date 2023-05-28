package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Location {
    private String name;
    private String country;
    private String city;
    @Ignore
    private Сoordinates coordinates;
    @Ignore
    private Weather weather;

    public String getName() {
        return name;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public Сoordinates getCoordinates() {
        return coordinates;
    }

    public Weather getWeather() {
        return weather;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public void setPoint(Сoordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }
    
}
