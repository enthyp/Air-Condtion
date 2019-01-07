package com.po.app.data.gios;

import com.po.app.data.IService;
import com.po.app.data.gios.model.index.Index;
import com.po.app.data.gios.model.index.IndexLevel;
import com.po.app.data.gios.model.measuring_station.MeasuringStation;
import com.po.app.data.gios.model.sensor.Param;
import com.po.app.data.gios.model.sensor.Sensor;
import com.po.app.data.gios.model.sensor_measurements.Measurements;
import com.po.app.data.gios.model.sensor_measurements.Value;
import com.po.app.data.gios.repository.IGiosDataSource;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Adapter for GiosDataSource instance.
 */
public class GiosService implements IService {

    private final DateTimeFormatter formatter;

    private final IGiosDataSource dataSource;

    public GiosService(IGiosDataSource dataSource) {
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.dataSource = dataSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Integer> getNameIdMap()
            throws ProcessingException, WebApplicationException {
        Map<String, Integer> nameIdMap = new HashMap<>();
        List<MeasuringStation> allStations = this.dataSource.findAllStations();

        for (MeasuringStation st : allStations)
            nameIdMap.put(st.getStationName(), st.getId());

        return nameIdMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringInstant getCurrentIndex(Integer stationId)
            throws ProcessingException, WebApplicationException {
        Index index = this.dataSource.getIndex(stationId);
        try {
            String indexDateTime = index.getStCalcDate();
            LocalDateTime dateTime;
            if (indexDateTime != null) {
                dateTime = LocalDateTime.parse(index.getStCalcDate(), this.formatter);
            } else {
                dateTime = null;
            }

            IndexLevel indexLevel = index.getStIndexLevel();
            String levelName;
            if (indexLevel != null) {
                levelName = indexLevel.getIndexLevelName();
            } else {
                levelName = null;
            }

            return new StringInstant(dateTime, levelName);
        } catch (DateTimeException exc) {
            throw new ProcessingException("Could not parse index date!");
        } catch (NullPointerException exc) {
            throw new ProcessingException("Null index returned!");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<LocalDateTime, String> getIndex(Integer stationId, List<LocalDateTime> dateTimes)
            throws ProcessingException, WebApplicationException {
        StringInstant currentIndex = this.getCurrentIndex(stationId);
        Map<LocalDateTime, String> resultMap = new HashMap<>();

        /* Here only one (current) index value is available (contrary to, say, Airly API). */
        if (dateTimes.isEmpty() || dateTimes.contains(currentIndex.dateTime)) {
            resultMap.put(currentIndex.dateTime, currentIndex.content);
        }

        return resultMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, DoubleInstant> getCurrentParamValue(Integer stationId, List<String> params)
            throws ProcessingException, WebApplicationException {
        Map<String, DoubleInstant> parNameValueMap = new HashMap<>();
        List<Sensor> sensors = this.dataSource.getSensors(stationId);

        // Go over all sensor available at this station.
        for (Sensor sensor : sensors) {
            Param param = sensor.getParam();
            if (param != null) {
                String paramCode = param.getParamCode();

                // Check if this sensor should be queried.
                if (params.isEmpty() || params.contains(paramCode)) {
                    int sensorId = sensor.getId();
                    Measurements measurements = this.dataSource.getSensorData(sensorId);

                    Value[] values = measurements.getValues();
                    if (values != null && values.length > 0) {
                        // Find current parameter value.
                        int closestMeasurementIdx = -1;
                        Duration minDistance = null;
                        LocalDateTime now = LocalDateTime.now();

                        for (int i = 0; i < values.length; i++) {
                            String dateTimeString = values[i].getDate();

                            if (dateTimeString != null) {
                                try {
                                    LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, this.formatter);
                                    Duration newDistance = Duration.between(now, dateTime);

                                    if (minDistance == null || newDistance.compareTo(minDistance) > 0) {
                                        closestMeasurementIdx = i;
                                        minDistance = newDistance;
                                    }
                                } catch (DateTimeException exc) {
                                    // Do nothing :)
                                }
                            }
                        }

                        // Check if current measurement datetime was found.
                        if (closestMeasurementIdx >= 0) {
                            float measurement = values[closestMeasurementIdx].getValue();
                            String dateTimeString = values[closestMeasurementIdx].getDate();
                            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, this.formatter);
                            parNameValueMap.put(paramCode, new DoubleInstant(dateTime, measurement));
                        }
                    }
                }
            }
        }

        return parNameValueMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<StringInstant, Double> getParamValue(Integer stationId,
                                                   List<LocalDateTime> dateTimes,
                                                   List<String> params)
            throws ProcessingException, WebApplicationException {
        Map<StringInstant, Double> parInstantValueMap = new HashMap<>();
        List<Sensor> sensors = this.dataSource.getSensors(stationId);

        // Go over all sensor available at this station.
        for (Sensor sensor : sensors) {
            Param param = sensor.getParam();
            if (param != null) {
                String paramCode = param.getParamCode();

                // Check if this sensor should be queried.
                if (params.isEmpty() || params.contains(paramCode)) {
                    int sensorId = sensor.getId();
                    Measurements measurements = this.dataSource.getSensorData(sensorId);

                    Value[] values = measurements.getValues();
                    if (values != null) {
                        for (Value value : values) {
                            String dateTimeString = value.getDate();
                            double measurementValue = value.getValue();

                            if (dateTimeString != null) {
                                try {
                                    LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, this.formatter);
                                    if (dateTimes.isEmpty() || dateTimes.contains(dateTime)) {
                                        StringInstant measurement = new StringInstant(dateTime, paramCode);
                                        parInstantValueMap.put(measurement, measurementValue);
                                    }
                                } catch (DateTimeException exc) {
                                    // Do nothing :))
                                }
                            }
                        }
                    }
                }
            }
        }

        return parInstantValueMap;
    }
}
