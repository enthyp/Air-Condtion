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

        for (MeasuringStation st : allStations) {
            // Validate - there must not be any null or empty values in the map.
            if (st.getId() != null && st.getStationName() != null && !st.getStationName().equals("")) {
                nameIdMap.put(st.getStationName(), st.getId());
            } else {
                // That's the place to inform the user about a provider issue.
                System.out.println(String.format(
                        "WARNING: Empty station name field for station ID: %d", st.getId())
                );
            }
        }

        if (nameIdMap.isEmpty()) {
            throw new ProcessingException("No valid 'station: ID' pairs provided!");
        }

        return nameIdMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringInstant getCurrentIndex(Integer stationId)
            throws ProcessingException, WebApplicationException {
        Index index = this.dataSource.getIndex(stationId);

        // Get date - may be null.
        String indexDateTime = index.getStCalcDate();
        LocalDateTime dateTime;
        if (indexDateTime != null) {
            try {
                dateTime = LocalDateTime.parse(indexDateTime, this.formatter);
            } catch (DateTimeException exc) {
                dateTime = null;
                System.out.println(String.format(
                        "WARNING: incorrect index date format provided: %s", indexDateTime));
            }
        } else {
            dateTime = null;
            System.out.println(String.format(
                    "WARNING: no index date provided for station %d!", stationId));
        }

        // Get index value - must not be empty nor null.
        IndexLevel indexLevel = index.getStIndexLevel();
        String levelName;
        if (indexLevel != null && !indexLevel.getIndexLevelName().equals("")) {
            levelName = indexLevel.getIndexLevelName();
        } else {
            throw new ProcessingException("Empty index value provided!");
        }

        return new StringInstant(dateTime, levelName);
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
     * The horror... the horror... Should be separated into smaller methods but there was no time.
     */
    @Override
    public Map<String, DoubleInstant> getCurrentParamValue(Integer stationId, List<String> params)
            throws ProcessingException, WebApplicationException {
        Map<String, DoubleInstant> parNameValueMap = new HashMap<>();
        List<Sensor> sensors = this.dataSource.getSensors(stationId);

        if (!sensors.isEmpty()) {
            boolean validSensorFound = false;
            // Go over all sensor available at this station.
            for (Sensor sensor : sensors) {
                Param param = sensor.getParam();
                if (param != null) {
                    String paramCode = param.getParamCode();

                    // Check if this sensor should be queried.
                    if ((paramCode != null && !paramCode.equals(""))
                            && (params.isEmpty() || params.contains(paramCode))) {
                        Integer sensorId = sensor.getId();
                        if (sensorId != null) {
                            try {
                                Measurements measurements = this.dataSource.getSensorData(sensorId);

                                Value[] values = measurements.getValues();
                                if (values != null && values.length > 0) {
                                    // Find current parameter value.
                                    Integer closestMeasurementIdx = null;
                                    Duration minDistance = null;
                                    LocalDateTime now = LocalDateTime.now();

                                    for (int i = 0; i < values.length; i++) {
                                        String dateTimeString = values[i].getDate();

                                        if (dateTimeString != null && values[i].getValue() != null) {
                                            validSensorFound = true;
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
                                    if (closestMeasurementIdx != null) {
                                        Float measurement = values[closestMeasurementIdx].getValue();
                                        String dateTimeString = values[closestMeasurementIdx].getDate();

                                        // Date and value were checked earlier.
                                        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, this.formatter);
                                        parNameValueMap.put(paramCode, new DoubleInstant(dateTime, measurement));

                                    }
                                } else {
                                    System.out.println(String.format(
                                            "WARNING: empty sensor data values provided for %s!", paramCode));
                                }
                            } catch (WebApplicationException exc) {
                                System.out.println(String.format(
                                        "Incorrect sensor id: %d", sensorId));
                            }
                        } else {
                            System.out.println("WARNING: empty sensor id provided!");
                        }
                    }
                } else {
                    System.out.println(String.format(
                            "WARNING: empty parameter for sensor %d!", sensor.getId()));
                }
            }

            if (!validSensorFound) {
                throw new ProcessingException("No valid sensor data provided for this station id!");
            }
        } else {
            throw new ProcessingException("No sensors provided for this station id!");
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

        if (!sensors.isEmpty()) {
            boolean validSensorFound = false;
            // Go over all sensor available at this station.
            for (Sensor sensor : sensors) {
                Param param = sensor.getParam();
                if (param != null) {
                    String paramCode = param.getParamCode();
                    // Check if this sensor should be queried.
                    if ((paramCode != null && !paramCode.equals(""))
                            && (params.isEmpty() || params.contains(paramCode))) {
                        Integer sensorId = sensor.getId();

                        if (sensorId != null) {
                            try {
                                Measurements measurements = this.dataSource.getSensorData(sensorId);

                                Value[] values = measurements.getValues();
                                if (values != null && values.length > 0) {
                                    for (Value value : values) {
                                        String dateTimeString = value.getDate();
                                        Float measurementValue = value.getValue();

                                        if (dateTimeString != null && measurementValue != null) {
                                            validSensorFound = true;
                                            try {
                                                LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, this.formatter);
                                                if (dateTimes.isEmpty() || dateTimes.contains(dateTime)) {
                                                    StringInstant measurement = new StringInstant(dateTime, paramCode);
                                                    parInstantValueMap.put(measurement, Double.valueOf(measurementValue));
                                                }
                                            } catch (DateTimeException exc) {
                                                // Do nothing :))
                                            }
                                        }
                                    }
                                } else {
                                    System.out.println(String.format(
                                            "WARNING: empty sensor data values provided for %s!", paramCode));
                                }
                            } catch (WebApplicationException exc) {
                                System.out.println(String.format(
                                        "Incorrect sensor id: %d", sensorId));
                            }
                        }
                    }
                } else {
                    System.out.println("WARNING: empty sensor id provided!");
                }
            }

            if (!validSensorFound) {
                throw new ProcessingException("No valid sensor data provided!");
            }
        } else {
            throw new ProcessingException("No sensors provided for this station id!");
        }

        return parInstantValueMap;
    }
}
