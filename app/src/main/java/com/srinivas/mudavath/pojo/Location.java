package com.srinivas.mudavath.pojo;

import java.io.Serializable;

/**
 * Created by Mudavath Srinivas on 21-02-2016.
 */
public class Location implements Serializable{

    private String latitude;
    private String longitude;

    public Location(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
