package com.po.app.data.gios.repository;

import com.po.app.data.gios.model.index.Index;
import com.po.app.data.gios.model.measuring_station.MeasuringStation;
import com.po.app.data.gios.model.sensor.Sensor;
import com.po.app.data.gios.model.sensor_measurements.Measurements;
import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GiosDataSource implements IGiosDataSource {
    private final String ENDPOINT_BASE = "http://api.gios.gov.pl/pjp-api/rest";
    private final String ENDPOINT_FIND_ALL = "station/findAll";
    private final String ENDPOINT_STATION_SENSORS = "station/sensors";
    private final String ENDPOINT_GET_DATA = "data/getData";
    private final String ENDPOINT_GET_INDEX = "aqindex/getIndex";

    private final WebTarget baseTarget;

    public GiosDataSource() {
        // TODO: use clientConfig?
        Client client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        client.property(ClientProperties.READ_TIMEOUT, 3000);
        baseTarget = client.target(ENDPOINT_BASE);
    }

    public List<MeasuringStation> findAllStations() throws ProcessingException, WebApplicationException {
        MeasuringStation[] allStations = baseTarget
                .path(ENDPOINT_FIND_ALL)
                .request(MediaType.APPLICATION_JSON)
                .get(MeasuringStation[].class);

        return new ArrayList<>(Arrays.asList(allStations));
    }

    public List<Sensor> getSensors(int stationId) throws ProcessingException, WebApplicationException {
        Sensor[] sensors = baseTarget
                .path(ENDPOINT_STATION_SENSORS)
                .path(String.valueOf(stationId))
                .request(MediaType.APPLICATION_JSON)
                .get(Sensor[].class);
        return new ArrayList<>(Arrays.asList(sensors));
    }

    public Measurements getSensorData(int sensorId) throws ProcessingException, WebApplicationException {
        return baseTarget
                .path(ENDPOINT_GET_DATA)
                .path(String.valueOf(sensorId))
                .request(MediaType.APPLICATION_JSON)
                .get(Measurements.class);
    }

    public Index getIndex(int stationId) throws ProcessingException, WebApplicationException {
        System.out.println("BOB");
        return baseTarget
                .path(ENDPOINT_GET_INDEX)
                .path(String.valueOf(stationId))
                .request(MediaType.APPLICATION_JSON)
                .get(Index.class);
    }
}
