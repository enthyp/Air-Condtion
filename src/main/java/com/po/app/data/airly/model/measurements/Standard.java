package com.po.app.data.airly.model.measurements;

public class Standard {
    private String limit;

    private String percent;

    private String name;

    private String pollutant;

    public String getLimit ()
    {
        return limit;
    }

    public void setLimit (String limit)
    {
        this.limit = limit;
    }

    public String getPercent ()
    {
        return percent;
    }

    public void setPercent (String percent)
    {
        this.percent = percent;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getPollutant ()
    {
        return pollutant;
    }

    public void setPollutant (String pollutant)
    {
        this.pollutant = pollutant;
    }

    @Override
    public String toString()
    {
        return "Standard [limit = "+limit+", percent = "+percent+", name = "+name+", pollutant = "+pollutant+"]";
    }
}
