package com.po.app.data.gios.model.sensor;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class Param implements Serializable {
    @JsonProperty
    private String paramName;

    @JsonProperty
    private String paramFormula;

    @JsonProperty
    private String paramCode;

    @JsonProperty
    private int idParam;

    public int getIdParam ()
    {
        return idParam;
    }

    public void setIdParam (int idParam)
    {
        this.idParam = idParam;
    }

    public String getParamFormula ()
    {
        return paramFormula;
    }

    public void setParamFormula (String paramFormula)
    {
        this.paramFormula = paramFormula;
    }

    public String getParamName ()
    {
        return paramName;
    }

    public void setParamName (String paramName)
    {
        this.paramName = paramName;
    }

    public String getParamCode ()
    {
        return paramCode;
    }

    public void setParamCode (String paramCode)
    {
        this.paramCode = paramCode;
    }

    @Override
    public String toString() {
        return "Param{" +
                "idParam='" + idParam + '\'' +
                ", paramFormula='" + paramFormula + '\'' +
                ", paramName='" + paramName + '\'' +
                ", paramCode='" + paramCode + '\'' +
                '}';
    }
}
