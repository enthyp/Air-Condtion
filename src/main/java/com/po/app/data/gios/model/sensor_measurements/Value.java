package com.po.app.data.gios.model.sensor_measurements;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Value implements Serializable {
    @JsonProperty
    private String date;

    @JsonProperty
    private float value;

    public float getValue ()
    {
        return value;
    }

    public void setValue (float value)
    {
        this.value = value;
    }

    public String getDate ()
    {
        return date;
    }

    public void setDate (String date)
    {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Value{" +
                "date='" + date + '\'' +
                ", value=" + value +
                '}';
    }
}

