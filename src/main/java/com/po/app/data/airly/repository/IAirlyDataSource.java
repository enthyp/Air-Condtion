package com.po.app.data.airly.repository;

import com.po.app.data.airly.model.installation.Installation;
import com.po.app.data.airly.model.measurements.Measurements;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import java.util.List;

public interface IAirlyDataSource {

    List<Installation> findAllInstallations() throws ProcessingException, WebApplicationException;

    Measurements getMeasurements(int installationId) throws ProcessingException, WebApplicationException;
}