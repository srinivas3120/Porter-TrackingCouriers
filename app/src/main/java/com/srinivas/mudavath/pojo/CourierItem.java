package com.srinivas.mudavath.pojo;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Mudavath Srinivas on 21-02-2016.
 */
public class CourierItem implements Serializable{
    private String name;
    private String url;
    private String type;
    private String weight;
    private int quantity;
    private String value;
    private String color;
    private String eta;
    private Location currentLocation;

    public CourierItem(){

    }

    public CourierItem(String name, String url, String type, String weight, int quantity, String value, String color, String eta, Location currentLocation) {
        this.name = name;
        this.url = url;
        this.type = type;
        this.weight = weight;
        this.quantity = quantity;
        this.value = value;
        this.color = color;
        this.eta = eta;
        this.currentLocation = currentLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}
