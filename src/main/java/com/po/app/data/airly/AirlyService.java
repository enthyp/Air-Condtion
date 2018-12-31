package com.po.app.data.airly;

import com.po.app.data.Service;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


public class AirlyService implements Service {
    @Override
    public Map<String, Integer> getNameIdMap() {
        return null;
    }

    @Override
    public StringInstant getCurrentIndex(Integer stationId)
            throws ProcessingException, WebApplicationException {
        return null;
    }

    @Override
    public Map<LocalDateTime, String> getIndex(Integer stationId, List<LocalDateTime> dateTimes)
            throws ProcessingException, WebApplicationException {
        return null;
    }

    @Override
    public Map<String, DoubleInstant> getCurrentParamValue(Integer stationId, List<String> params)
            throws ProcessingException, WebApplicationException {
        return null;
    }

    @Override
    public Map<StringInstant, Double> getParamValue(Integer stationId,
                                                    List<LocalDateTime> dateTimes,
                                                    List<String> params)
            throws ProcessingException, WebApplicationException {
        return null;
    }
}
