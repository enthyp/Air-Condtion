package com.po.app.data;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * A wrapper on the object representation of air condition API. It provides uniform basic methods
 * that can be used for further combining air quality data.
 * It assumes that implementations are based on JAX-RS Client API, hence the types of exceptions thrown.
 */
public interface IService {

    /**
     * A representation of e.g. parameter name or index level at given point in time.
     */
    class StringInstant {
        public LocalDateTime dateTime;
        public String content;

        public StringInstant(LocalDateTime dateTime, String name) {
            this.dateTime = dateTime;
            this.content = name;
        }

        @Override
        public String toString() {
            return "StringInstant{" +
                    "dateTime=" + dateTime +
                    ", content='" + content + '\'' +
                    '}';
        }
    };

    /**
     * A representation of parameter or index value at given point in time.
     */
    class DoubleInstant {
        public LocalDateTime dateTime;
        public double value;

        public DoubleInstant(LocalDateTime dateTime, double value) {
            this.dateTime = dateTime;
            this.value = value;
        }

        public LocalDateTime getDateTime() {
            return this.dateTime;
        }

        @Override
        public String toString() {
            return "DoubleInstant{" +
                    "dateTime=" + dateTime +
                    ", value=" + value +
                    '}';
        }
    }

    /**
     * Returns a map from measuring station's name to its ID.
     * @return map from name of a station (certain word sequence that uniquely identifies a station)
     * to station's ID.
     */
    Map<String, Integer> getNameIdMap()
            throws ProcessingException, WebApplicationException;

    /**
     * Returns current value of air quality index for given measuring station.
     * @param stationId ID of measuring station.
     * @return pair (indexValue, timeInstant), where `timeInstant` is the point in time at which `indexValue`
     *         was calculated.
     */
    StringInstant getCurrentIndex(Integer stationId)
            throws ProcessingException, WebApplicationException;

    /**
     * Returns a map from time instant to a level of an air quality index for given measuring station.
     * @param stationId ID of measuring station.
     * @param dateTimes list of time instants for which to return index values. If empty, all available index
     *                  levels are returned.
     * @return map from time instant to level of air quality index.
     */
    Map<LocalDateTime, String> getIndex(Integer stationId, List<LocalDateTime> dateTimes)
            throws ProcessingException, WebApplicationException;

    /**
     * Returns a map from parameter name to current value of parameter for given measuring station.
     * @param stationId ID of measuring station.
     * @param params list of parameter names for which to return values. If empty, values for all available
     *               parameters are returned.
     * @return map from parameter name to pair (parameterValue, timeInstant), where `timeInstant` is the point
     *         in time at which current `parameterValue` was measured.
     */
    Map<String, DoubleInstant> getCurrentParamValue(Integer stationId, List<String> params)
            throws ProcessingException, WebApplicationException;

    /**
     * Returns a map from pair (parameterName, timeInstant) to a value of parameter `parameterName` for
     * given measuring station.
     * @param stationId ID of measuring station.
     * @param dateTimes list of time instants for which to return values of given parameters. If empty,
     *                  then values for all available past time instants are returned.
     * @param params list of parameter names for which to return values. If empty, values for all available
     *               parameters are returned.
     * @return map from pair (parameterName, timeInstant) to value of parameter `parameterName`.
     */
    Map<StringInstant, Double> getParamValue(Integer stationId,
                                                 List<LocalDateTime> dateTimes,
                                                 List<String> params)
            throws ProcessingException, WebApplicationException;
}
