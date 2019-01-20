package com.po.app.data.gios.model.sensor;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Sensor implements Serializable
{
    @JsonProperty
    private Integer id;

    @JsonProperty
    private Integer stationId;

    @JsonProperty
    private Param param;

    public Integer getId ()
    {
        return id;
    }

    public void setId (Integer id)
    {
        this.id = id;
    }

    public Param getParam ()
    {
        return param;
    }

    public void setParam (Param param)
    {
        this.param = param;
    }

    public Integer getStationId ()
    {
        return stationId;
    }

    public void setStationId (Integer stationId)
    {
        this.stationId = stationId;
    }

    @Override
    public String toString() {
        return "Sensor{" +
                "id='" + id + '\'' +
                ", param=" + param +
                ", stationId='" + stationId + '\'' +
                '}';
    }
}
