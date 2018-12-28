package com.po.app.REST;

import com.fasterxml.jackson.annotation.JsonProperty;

public class City {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;
    @JsonProperty
    private Commune commune;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Commune getCommune() {
        return commune;
    }

    public void setCommune(Commune commune) {
        this.commune = commune;
    }

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", commune=" + commune +
                '}';
    }
}
