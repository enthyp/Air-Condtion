package com.po.app.data.gios.repository;

import com.po.app.data.gios.model.index.Index;
import com.po.app.data.gios.model.measuring_station.MeasuringStation;
import com.po.app.data.gios.model.sensor.Sensor;
import com.po.app.data.gios.model.sensor_measurements.Measurements;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import java.util.List;

/**
 * Interface of REST client for GIOS air condition API.
 */
public interface IGiosDataSource {

    /**
     * Returns all measuring stations available from API.
     * @return all available measuring stations.
     */
    List<MeasuringStation> findAllStations() throws ProcessingException, WebApplicationException;

    /**
     * Returns all sensors (e.g. CO, NO2 sensors) available for given measuring station.
     * @param stationId for sensors of which station to look at.
     * @return all sensors available for given station.
     */
    List<Sensor> getSensors(int stationId) throws ProcessingException, WebApplicationException;

    /**
     * Get all measurements for given sensor.
     * @param sensorId sensor for which to obtain measurement values.
     * @return all measurements available for given sensor.
     */
    Measurements getSensorData(int sensorId) throws ProcessingException, WebApplicationException;

    /**
     * Get current air quality index for given station.
     * @param stationId for which station to obtain the index.
     * @return air quality index description.
     */
    Index getIndex(int stationId) throws ProcessingException, WebApplicationException;
}
