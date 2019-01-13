package com.po.app.data.gios.model.measuring_station;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class City implements Serializable {
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
