package com.po.app.data.gios.model.sensor_measurements;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Arrays;

public class Measurements {
    @JsonProperty
    private String key;

    @JsonProperty
    private Value[] values;

    public Value[] getValues ()
    {
        return values;
    }

    public void setValues (Value[] values)
    {
        this.values = values;
    }

    public String getKey ()
    {
        return key;
    }

    public void setKey (String key)
    {
        this.key = key;
    }

    @Override
    public String toString() {
        return "Measurements{" +
                "key='" + key + '\'' +
                ", values=" + Arrays.toString(values) +
                '}';
    }
}

