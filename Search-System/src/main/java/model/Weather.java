package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Weather {   
    @JsonProperty
    private int temp; 
    private static final int CONVERT_CALV_TO_CEL = -273;

    public int getTemp() {
        return temp;
    }

    public void setTemp(float temp) {
        this.temp = (int) (temp + CONVERT_CALV_TO_CEL);
    }
}
