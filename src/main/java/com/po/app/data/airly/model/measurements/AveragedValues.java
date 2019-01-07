package com.po.app.data.airly.model.measurements;

public class AveragedValues {
    private Standard[] standards;

    private String tillDateTime;

    private Value[] values;

    private Index[] indexes;

    private String fromDateTime;

    public Standard[] getStandards ()
    {
        return standards;
    }

    public void setStandards (Standard[] standards)
    {
        this.standards = standards;
    }

    public String getTillDateTime ()
    {
        return tillDateTime;
    }

    public void setTillDateTime (String tillDateTime)
    {
        this.tillDateTime = tillDateTime;
    }

    public Value[] getValues ()
    {
        return values;
    }

    public void setValues (Value[] values)
    {
        this.values = values;
    }

    public Index[] getIndexes ()
    {
        return indexes;
    }

    public void setIndexes (Index[] indexes)
    {
        this.indexes = indexes;
    }

    public String getFromDateTime ()
    {
        return fromDateTime;
    }

    public void setFromDateTime (String fromDateTime)
    {
        this.fromDateTime = fromDateTime;
    }

    @Override
    public String toString()
    {
        return "AveragedValues [standards = "+standards+", tillDateTime = "+tillDateTime+", values = "+values+", indexes = "+indexes+", fromDateTime = "+fromDateTime+"]";
    }
}