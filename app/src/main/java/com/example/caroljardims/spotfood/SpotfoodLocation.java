package com.example.caroljardims.spotfood;



public class SpotfoodLocation {
    private String id;
    private String name;
    private String lat;
    private String lon;
    private String logo;
    private String rate;
    private Integer status;
    private String type;
    private int visitors;

    SpotfoodLocation() {}

    SpotfoodLocation(String id, String name, String lat, String lon, String logo, String rate, Integer status, String type, int visitors) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.logo = logo;
        this.rate = rate;
        this.visitors = visitors;
        this.status = status;
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLat() {
        return this.lat;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getLon() {
        return this.lon;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getLogo() {
        return this.logo;
    }

    public void setRate(String rate) { this.rate = rate; }

    public String getRate() { return this.rate; }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStatus() {
        return this.status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setVisitors() { this.visitors += 1; }

    public int getVisitors() { return this.visitors; }

}
