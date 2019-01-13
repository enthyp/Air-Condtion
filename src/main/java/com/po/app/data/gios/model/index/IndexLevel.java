package com.po.app.data.gios.model.index;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class IndexLevel implements Serializable {
    @JsonProperty
    private int id;

    @JsonProperty
    private String indexLevelName;

    public int getId ()
    {
        return id;
    }

    public void setId (int id)
    {
        this.id = id;
    }

    public String getIndexLevelName ()
    {
        return indexLevelName;
    }

    public void setIndexLevelName (String indexLevelName)
    {
        this.indexLevelName = indexLevelName;
    }

    @Override
    public String toString() {
        return "IndexLevel{" +
                "id=" + id +
                ", indexLevelName='" + indexLevelName + '\'' +
                '}';
    }
}
