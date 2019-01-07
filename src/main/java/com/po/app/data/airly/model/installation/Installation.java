package com.po.app.data.airly.model.installation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Installation {
    @JsonProperty
    private int id;

    @JsonProperty
    private Address address;

    @JsonProperty
    private Location location;

    @JsonProperty
    private float elevation;

    @JsonProperty
    private boolean airly;

    @JsonProperty
    private Sponsor sponsor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public float getElevation() {
        return elevation;
    }

    public void setElevation(float elevation) {
        this.elevation = elevation;
    }

    public boolean isAirly() {
        return airly;
    }

    public void setAirly(boolean airly) {
        this.airly = airly;
    }

    public Sponsor getSponsor() {
        return sponsor;
    }

    public void setSponsor(Sponsor sponsor) {
        this.sponsor = sponsor;
    }

    @Override
    public String toString() {
        return "Installation{" +
                "id=" + id +
                ", address=" + address +
                ", location=" + location +
                ", elevation=" + elevation +
                ", airly=" + airly +
                ", sponsor=" + sponsor +
                '}';
    }
}
