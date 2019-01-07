package com.po.app.data.gios.repository;

import com.po.app.data.gios.model.index.Index;
import com.po.app.data.gios.model.measuring_station.MeasuringStation;
import com.po.app.data.gios.model.sensor.Sensor;
import com.po.app.data.gios.model.sensor_measurements.Measurements;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
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

    public GiosCachedDataSource(IGiosDataSource dataSource) {
        super(dataSource);
    }

    @Override
    public List<MeasuringStation> findAllStations() throws ProcessingException, WebApplicationException {
        return null;
    }

    @Override
    public List<Sensor> getSensors(int stationId) throws ProcessingException, WebApplicationException {
        return null;
    }

    @Override
    public Measurements getSensorData(int sensorId) throws ProcessingException, WebApplicationException {
        return null;
    }

    @Override
    public Index getIndex(int stationId) throws ProcessingException, WebApplicationException {
        return null;
    }
}
