package com.po.app.data.gios.repository;

import com.po.app.data.gios.model.index.Index;
import com.po.app.data.gios.model.measuring_station.MeasuringStation;
import com.po.app.data.gios.model.sensor.Sensor;
import com.po.app.data.gios.model.sensor_measurements.Measurements;

import java.io.IOException;
import java.util.List;

public interface IGiosCache {

    List<MeasuringStation> findAllStations() throws IOException;
    void setAllStations(List<MeasuringStation> stations) throws IOException;

    List<Sensor> getSensors(int stationId) throws IOException;
    void setSensors(List<Sensor> sensors, int stationId) throws IOException;

    Measurements getSensorData(int sensorId) throws IOException;
    void setSensorData(Measurements measurements, int sensorId) throws IOException;

    Index getIndex(int stationId) throws IOException;
    void setIndex(Index index, int stationId) throws IOException;

    // TODO: add separate methods for getting and setting cached values
    // TODO: setting should take a flag that would mean old version should be deleted
    // TODO: it should be possible to choose (command line) whether to use cached or non-cached version.
}
