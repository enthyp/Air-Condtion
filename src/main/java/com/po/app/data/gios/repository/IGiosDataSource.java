package com.po.app.data.gios.repository;

import com.po.app.data.gios.model.index.Index;
import com.po.app.data.gios.model.measuring_station.MeasuringStation;
import com.po.app.data.gios.model.sensor.Sensor;
import com.po.app.data.gios.model.sensor_measurements.Measurements;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import java.util.List;

public interface IGiosDataSource {

    List<MeasuringStation> findAllStations() throws ProcessingException, WebApplicationException;

    List<Sensor> getSensors(int stationId) throws ProcessingException, WebApplicationException;

    Measurements getSensorData(int sensorId) throws ProcessingException, WebApplicationException;

    Index getIndex(int stationId) throws ProcessingException, WebApplicationException;
}
