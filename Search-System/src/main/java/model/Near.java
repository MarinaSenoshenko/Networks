package model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Near {
    private String name;
    private String country;
    private String city;
    @Ignore
    private String description;
    @Ignore
    private String xid;

    public String getName() {
        return name;
    }

    public String getXid() {
        return xid;
    }

    public String getDescription() {
        return description;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setXid(String xid) {
        this.xid = xid;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
}
