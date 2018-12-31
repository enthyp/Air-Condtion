package com.po.app.data.airly;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public class AirlyDataSource {
    private final String ENDPOINT_BASE = "https://airapi.airly.eu/v2";
    private final String ENDPOINT_FIND_ALL_NEAREST = "installations/nearest?lat=0&lng=0&maxDistanceKM=-1&maxResults=-1";

    private final String ACCEPT_HEADER = "Accept";
    private final String ACCEPT_VALUE = "application/json";
    private final String API_KEY_HEADER = "apikey";
    private final String API_KEY;

    private final WebTarget baseTarget;

    public AirlyDataSource(String API_KEY) {
        this.API_KEY = API_KEY;
        // TODO: use clientConfig?
        Client client = ClientBuilder.newClient();
        baseTarget = client.target(ENDPOINT_BASE);
    }


}
