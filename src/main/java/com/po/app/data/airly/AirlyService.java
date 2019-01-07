package com.po.app.data.airly;

import com.po.app.data.IService;
import com.po.app.data.airly.model.installation.Address;
import com.po.app.data.airly.model.installation.Installation;
import com.po.app.data.airly.model.installation.Location;
import com.po.app.data.airly.model.measurements.AveragedValues;
import com.po.app.data.airly.model.measurements.Index;
import com.po.app.data.airly.model.measurements.Measurements;
import com.po.app.data.airly.model.measurements.Value;
import com.po.app.data.airly.repository.IAirlyDataSource;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Adapter for AirlyDataSource instance.
 */
public class AirlyService implements IService {

    private final DateTimeFormatter formatter;

    private final IAirlyDataSource dataSource;

    public AirlyService(IAirlyDataSource dataSource) {
        this.formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]'Z'");
        this.dataSource = dataSource;
    }

    /**
     * {@inheritDoc}
     * Airly API does not provide explicit names of installations so for the purpose of the application
     * the names are formed by concatenation of address and location components using underscore character ('_').
     */
    @Override
    public Map<String, Integer> getNameIdMap()
            throws ProcessingException, WebApplicationException {
        Map<String, Integer> nameIdMap = new HashMap<>();
        List<Installation> allInstallations = this.dataSource.findAllInstallations();

        for (Installation installation : allInstallations) {
            StringBuilder nameB = new StringBuilder();
            Address address = installation.getAddress();
            Location location = installation.getLocation();
            nameB.append(address.getCountry()).append("_")
                    .append(address.getCity()).append("_")
                    .append(address.getStreet()).append("_")
                    .append(address.getNumber()).append("_")
                    .append(address.getDisplayAddress1()).append("_")
                    .append(address.getDisplayAddress2()).append("_")
                    .append(location.getLatitude()).append("_")
                    .append(location.getLongitude());
            nameIdMap.put(nameB.toString(), installation.getId());
        }

        return nameIdMap;
    }

    /**
     * Returns level of Airly CAQI index from given list of indices.
     * @param indices indices to search through.
     * @return level of the index in the format 'name: value' (null if CAQI not found).
     */
    private String getCAQIIndexLevel(Index[] indices) {
        String level = null;
        for (Index ind : indices) {
            if (ind.getName().equals("AIRLY_CAQI")) {
                level = ind.getLevel() + ": " + ind.getValue();
                break;
            }
        }
        return level;
    }

    /**
     * Parses datetime string to LocalDateTime instance.
     * @param dateTimeString text representation of date in "yyyy-MM-dd'T'HH:mm:ss[.SSS]'Z'" format.
     * @return corresponding LocalDateTime instance.
     */
    private LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, this.formatter)
                .truncatedTo(ChronoUnit.SECONDS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringInstant getCurrentIndex(Integer stationId)
            throws ProcessingException, WebApplicationException {
        // TODO: incorrect stationId must be handled at application level!
        Measurements measurements = this.dataSource.getMeasurements(stationId);
        AveragedValues current = measurements.getCurrent();
        Index[] indices = current.getIndexes();

        try {
            LocalDateTime dateTime = this.parseDateTime(current.getTillDateTime());
            String level = this.getCAQIIndexLevel(indices);
            return new StringInstant(dateTime, level);
        } catch (DateTimeException exc) {
            throw new ProcessingException("Could not parse index date! " + exc.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<LocalDateTime, String> getIndex(Integer stationId, List<LocalDateTime> dateTimes)
            throws ProcessingException, WebApplicationException {
        Map<LocalDateTime, String> resultMap = new HashMap<>();

        Measurements measurements = this.dataSource.getMeasurements(stationId);
        AveragedValues current = measurements.getCurrent();
        AveragedValues[] history = measurements.getHistory();

        LocalDateTime currentDatetime = this.parseDateTime(current.getTillDateTime());
        resultMap.put(currentDatetime, this.getCAQIIndexLevel(current.getIndexes()));

        for (AveragedValues averagedValues : history) {
            LocalDateTime dateTime = this.parseDateTime(averagedValues.getTillDateTime());
            resultMap.put(dateTime, this.getCAQIIndexLevel(averagedValues.getIndexes()));
        }

        if (!dateTimes.isEmpty())
            resultMap.keySet().retainAll(dateTimes);
        return resultMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, DoubleInstant> getCurrentParamValue(Integer stationId, List<String> params)
            throws ProcessingException, WebApplicationException {
        Map<String, DoubleInstant> resultMap = new HashMap<>();

        Measurements measurements = this.dataSource.getMeasurements(stationId);
        AveragedValues current = measurements.getCurrent();
        LocalDateTime dateTime = this.parseDateTime(current.getTillDateTime());

        Value[] values = current.getValues();
        for (Value value : values) {
            if (params.isEmpty() || params.contains(value.getName())) {
                resultMap.put(value.getName(), new DoubleInstant(dateTime, value.getValue()));
            }
        }

        return resultMap;
    }

    /**
     * Utility method that returns a map from pair (parameterName, timeInstant) to a value of parameter
     * `parameterName` for given AveragedValues instance.
     * @param averagedValues all measurements for given station at given point in time.
     * @param dateTimes list of time instants for which to return values of given parameters. Only when
     *                  it is empty or it contains datetime of this AveragedValues instance a non-empty
     *                  result can be returned.
     * @param params list of parameter names for which to return values. If empty, values for all available
     *               parameters are returned.
     * @return map from pair (parameterName, timeInstant) to value of parameter `parameterName`.
     */
    private Map<StringInstant, Double> getParamsFromAveragedValues(AveragedValues averagedValues,
                                                                   List<LocalDateTime> dateTimes,
                                                                   List<String> params) {
        Map<StringInstant, Double> resultMap = new HashMap<>();
        LocalDateTime dateTime = this.parseDateTime(averagedValues.getTillDateTime());

        if (dateTimes.isEmpty() || dateTimes.contains(dateTime)) {
            Value[] values = averagedValues.getValues();
            for (Value value : values) {
                if (params.isEmpty() || params.contains(value.getName())) {
                    resultMap.put(new StringInstant(dateTime, value.getName()), value.getValue());
                }
            }
        }

        return resultMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<StringInstant, Double> getParamValue(Integer stationId,
                                                    List<LocalDateTime> dateTimes,
                                                    List<String> params)
            throws ProcessingException, WebApplicationException {
        Map<StringInstant, Double> resultMap = new HashMap<>();

        Measurements measurements = this.dataSource.getMeasurements(stationId);
        AveragedValues current = measurements.getCurrent();
        resultMap.putAll(this.getParamsFromAveragedValues(current, dateTimes, params));

        AveragedValues[] history = measurements.getHistory();
        for (AveragedValues averagedValues : history) {
            resultMap.putAll(this.getParamsFromAveragedValues(averagedValues, dateTimes, params));
        }

        return resultMap;
    }
}
