package com.po.app.data.airly.model.measurements;

import java.util.Arrays;

public class Measurements {
    private AveragedValues current;

    private AveragedValues[] history;

    private AveragedValues[] forecast;

    public AveragedValues getCurrent ()
    {
        return current;
    }

    public void setCurrent (AveragedValues current)
    {
        this.current = current;
    }

    public AveragedValues[] getHistory() {
        return history;
    }

    public void setHistory(AveragedValues[] history) {
        this.history = history;
    }

    public AveragedValues[] getForecast() {
        return forecast;
    }

    public void setForecast(AveragedValues[] forecast) {
        this.forecast = forecast;
    }

    @Override
    public String toString() {
        return "Measurements{" +
                "current=" + current +
                ", history=" + Arrays.toString(history) +
                ", forecast=" + Arrays.toString(forecast) +
                '}';
    }
}