package com.po.app.data.airly.repository;

import com.po.app.data.airly.model.installation.Installation;
import com.po.app.data.airly.model.measurements.Measurements;
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


public class AirlyDataSource implements IAirlyDataSource {
    private final String ENDPOINT_BASE = "https://airapi.airly.eu/v2";
    private final String ENDPOINT_FIND_ALL_NEAREST = "installations/nearest";
    private final String ENDPOINT_GET_MEASUREMENTS = "measurements/installation";

    private final String ACCEPT_HEADER = "Accept";
    private final String ACCEPT_VALUE = "application/json";
    private final String API_KEY_HEADER = "apikey";
    private final String API_KEY;

    private final WebTarget baseTarget;

    public AirlyDataSource(String API_KEY) {
        this.API_KEY = API_KEY;
        // TODO: use clientConfig?
        Client client = ClientBuilder.newClient();
        client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
        client.property(ClientProperties.READ_TIMEOUT, 3000);
        baseTarget = client.target(ENDPOINT_BASE);
    }

    public List<Installation> findAllInstallations() throws ProcessingException, WebApplicationException {
        Installation[] allInstallations =  baseTarget
                .path(ENDPOINT_FIND_ALL_NEAREST)
                .queryParam("lat", "50")
                .queryParam("lng", "50")
                .queryParam("maxDistanceKM", "-1")
                .queryParam("maxResults", "-1")
                .request(MediaType.APPLICATION_JSON)
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(API_KEY_HEADER, API_KEY)
                .get(Installation[].class);

        return new ArrayList<>(Arrays.asList(allInstallations));
    }

    public Measurements getMeasurements(int installationId) throws ProcessingException, WebApplicationException {
        return baseTarget
                .path(ENDPOINT_GET_MEASUREMENTS)
                .queryParam("installationId", installationId)
                .request(MediaType.APPLICATION_JSON)
                .header(ACCEPT_HEADER, ACCEPT_VALUE)
                .header(API_KEY_HEADER, API_KEY)
                .get(Measurements.class);
    }
}
