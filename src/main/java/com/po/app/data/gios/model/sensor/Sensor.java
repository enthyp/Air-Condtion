package com.po.app.data.gios.model.sensor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sensor
{
    @JsonProperty
    private int id;

    @JsonProperty
    private int stationId;

    @JsonProperty
    private Param param;

    public int getId ()
    {
        return id;
    }

    public void setId (int id)
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

    public int getStationId ()
    {
        return stationId;
    }

    public void setStationId (int stationId)
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
