package com.po.app.data.airly.model.installation;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Address {
    @JsonProperty
    private String displayAddress2;

    @JsonProperty
    private String street;

    @JsonProperty
    private String displayAddress1;

    @JsonProperty
    private String number;

    @JsonProperty
    private String city;

    @JsonProperty
    private String country;

    public String getDisplayAddress2() {
        return displayAddress2 == null ? "" : displayAddress2;
    }

    public void setDisplayAddress2(String displayAddress2) {
        this.displayAddress2 = displayAddress2;
    }

    public String getStreet() {
        return street == null ? "" : street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDisplayAddress1() {
        return displayAddress1 == null ? "" : displayAddress1;
    }

    public void setDisplayAddress1(String displayAddress1) {
        this.displayAddress1 = displayAddress1;
    }

    public String getNumber() {
        return number == null ? "" : number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city == null ? "" : city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country == null ? "" : country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return "Address{" +
                "displayAddress2='" + displayAddress2 + '\'' +
                ", street='" + street + '\'' +
                ", displayAddress1='" + displayAddress1 + '\'' +
                ", number='" + number + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
