package com.po.app.data.gios.repository;

import com.po.app.data.gios.model.index.Index;
import com.po.app.data.gios.model.measuring_station.MeasuringStation;
import com.po.app.data.gios.model.sensor.Sensor;
import com.po.app.data.gios.model.sensor_measurements.Measurements;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.List;

public class GiosCachedDataSource extends GiosDataSourceDecorator {

    /*
        IDEAS:
            -> It seems that access to methods that use cache should be synchronized - so that no other thread
               can call that method at the same time (important - this application ONLY constructs one instance
               at a time - singleton pattern (make it explicit??? within the factory most likely)).
            -> Moreover, in the cache implementation itself access to a single file should be performed using
               a FileLock (https://stackoverflow.com/questions/128038/how-can-i-lock-a-file-using-java-if-possible).
               Then no other JVM process (like the same application running from other terminal) will be able to
               access that file.
     */

    private IGiosCache cache;

    public GiosCachedDataSource(IGiosDataSource dataSource) throws IOException {
        super(dataSource);
        this.cache = new GiosCache();
    }

    @Override
    public synchronized List<MeasuringStation> findAllStations() throws ProcessingException, WebApplicationException {
        List<MeasuringStation> stations = null;

        try {
            stations = this.cache.findAllStations();
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
        }

        if (stations == null) {
            stations = this.dataSource.findAllStations();
            try {
                this.cache.setAllStations(stations);
            } catch (IOException exc) {
                System.out.println(exc.getMessage());
            }
        }

        return stations;
    }

    @Override
    public synchronized List<Sensor> getSensors(int stationId) throws ProcessingException, WebApplicationException {
        List<Sensor> sensors = null;

        try {
            sensors = this.cache.getSensors(stationId);
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
        }

        if (sensors == null) {
            sensors = this.dataSource.getSensors(stationId);
            try {
                this.cache.setSensors(sensors, stationId);
            } catch (IOException exc) {
                System.out.println(exc.getMessage());
            }
        }

        return sensors;
    }

    @Override
    public synchronized Measurements getSensorData(int sensorId) throws ProcessingException, WebApplicationException {
        Measurements measurements = null;

        try {
            measurements = this.cache.getSensorData(sensorId);
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
        }

        if (measurements == null) {
            measurements = this.dataSource.getSensorData(sensorId);
            try {
                this.cache.setSensorData(measurements, sensorId);
            } catch (IOException exc) {
                System.out.println(exc.getMessage());
            }
        }

        return measurements;
    }

    @Override
    public synchronized Index getIndex(int stationId) throws ProcessingException, WebApplicationException {
        Index index = null;

        try {
            index = this.cache.getIndex(stationId);
        } catch (IOException exc) {
            System.out.println(exc.getMessage());
        }

        if (index == null) {
            index = this.dataSource.getIndex(stationId);
            try {
                this.cache.setIndex(index, stationId);
            } catch (IOException exc) {
                System.out.println(exc.getMessage());
            }
        }

        return index;
    }
}
