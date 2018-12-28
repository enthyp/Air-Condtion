package com.po.app;

import com.po.app.REST.MeasuringStation;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class App 
{
    public static void main( String[] args )
    {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://api.gios.gov.pl/pjp-api/rest");
        WebTarget allWebTarget = webTarget.path("station/findAll");

        Invocation.Builder builder = allWebTarget.request(MediaType.APPLICATION_JSON);
        MeasuringStation[] response = builder.get(MeasuringStation[].class);
        for (MeasuringStation st : response)
            System.out.println(st);
    }

}
