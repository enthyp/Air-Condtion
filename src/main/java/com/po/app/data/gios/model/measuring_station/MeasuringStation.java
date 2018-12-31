package com.po.app.data.gios.model.measuring_station;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MeasuringStation {
    @JsonProperty
    private int id;

    @JsonProperty
    private String stationName;

    @JsonProperty
    private String gegrLat;

    @JsonProperty
    private String gegrLon;

    @JsonProperty
    private City city;

    @JsonProperty
    private String addressStreet;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getGegrLat() {
        return gegrLat;
    }

    public void setGegrLat(String gegrLat) {
        this.gegrLat = gegrLat;
    }

    public String getGegrLon() {
        return gegrLon;
    }

    public void setGegrLon(String gegrLon) {
        this.gegrLon = gegrLon;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    @Override
    public String toString() {
        return "MeasuringStation{" +
                "id=" + id +
                ", stationName='" + stationName + '\'' +
                ", gegrLat='" + gegrLat + '\'' +
                ", gegrLon='" + gegrLon + '\'' +
                ", city=" + city +
                ", addressStreet='" + addressStreet + '\'' +
                '}';
    }
}
