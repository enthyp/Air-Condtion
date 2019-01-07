package com.po.app.data.airly.model.measurements;

public class Index {
    private String level;

    private String color;

    private String description;

    private String name;

    private String value;

    private String advice;

    public String getLevel ()
    {
        return level;
    }

    public void setLevel (String level)
    {
        this.level = level;
    }

    public String getColor ()
    {
        return color;
    }

    public void setColor (String color)
    {
        this.color = color;
    }

    public String getDescription ()
    {
        return description;
    }

    public void setDescription (String description)
    {
        this.description = description;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getValue ()
    {
        return value;
    }

    public void setValue (String value)
    {
        this.value = value;
    }

    public String getAdvice ()
    {
        return advice;
    }

    public void setAdvice (String advice)
    {
        this.advice = advice;
    }

    @Override
    public String toString()
    {
        return "Index [level = "+level+", color = "+color+", description = "+description+", name = "+name+", value = "+value+", advice = "+advice+"]";
    }
}