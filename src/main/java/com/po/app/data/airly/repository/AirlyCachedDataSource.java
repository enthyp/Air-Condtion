package com.po.app.data.airly.repository;

import com.po.app.data.airly.model.installation.Installation;
import com.po.app.data.airly.model.measurements.Measurements;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.util.List;

public class AirlyCachedDataSource extends AirlyDataSourceDecorator {



    public AirlyCachedDataSource(IAirlyDataSource dataSource) throws IOException {
        super(dataSource);
    }

    @Override
    public List<Installation> findAllInstallations() throws ProcessingException, WebApplicationException {
        return null;
    }

    @Override
    public Measurements getMeasurements(int installationId) throws ProcessingException, WebApplicationException {
        return null;
    }
}
