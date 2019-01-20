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
     * <p>Admits natural chronological ordering.
     */
    class DoubleInstant implements Comparable<DoubleInstant> {
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

        @Override
        public int compareTo(DoubleInstant other) {
            return this.dateTime.compareTo(other.dateTime);
        }
    }

    /**
     * Returns a map from measuring station's name to its ID.
     * <p>It is warranted that there are no null nor empty values in the map. // TODO: handle ProcessingException
     * upon empty map
     *    // TODO: handle other server/processing errors.
     * <p>Warnings are printed out in case of null station name values in JSON.
     *
     * @return map from name of a station (certain word sequence that uniquely identifies a station)
     * to station's ID.
     * @throws ProcessingException if no valid 'station: ID' pairs are provided or there is an
     * incompatibility between expected and provided JSON response or other IO errors occur.
     */
    Map<String, Integer> getNameIdMap()
            throws ProcessingException, WebApplicationException;

    /**
     * Returns current value of air quality index for given measuring station.
     * <p> It is warranted that index value is not null nor empty, date may be null however, if not
     * provided or in incorrect format.
     * <p>Warnings are printed out in case of incorrect or missing index date.
     * @param stationId ID of measuring station. If incorrect then any server exceptions are transferred
     *                  (WebApplicationException) and if empty return value is provided - ProcessingException
     *                  is thrown.
     * @return pair (indexValue, timeInstant), where `timeInstant` is the point in time at which `indexValue`
     *         was calculated.
     * @throws ProcessingException if no index value is provided (which may happen if e.g. incorrect stationID
     * is passed as an argument) or there is an incompatibility between expected and provided JSON response
     * or other IO errors occur.
     */
    StringInstant getCurrentIndex(Integer stationId)
            throws ProcessingException, WebApplicationException;

    /**
     * Returns a map from time instant to a level of an air quality index for given measuring station.
     * Resulting map may be empty if no values are found fo given dates.
     * <p>It follows the same rules when it comes to exceptions as <i>getCurrentIndex</i> method.
     * @param stationId ID of measuring station.
     * @param dateTimes list of time instants for which to return index values. If empty, all available index
     *                  levels are returned.
     * @return map from time instant to level of air quality index.
     */
    Map<LocalDateTime, String> getIndex(Integer stationId, List<LocalDateTime> dateTimes)
            throws ProcessingException, WebApplicationException;

    /**
     * Returns a map from parameter name to current value of parameter for given measuring station.
     * @param stationId ID of measuring station. If incorrect an empty map may be returned or server side
     *                  exception (WebApplicationException) may be transferred.
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
