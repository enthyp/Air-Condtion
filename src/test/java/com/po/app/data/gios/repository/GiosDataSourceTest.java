package com.po.app.data.gios.repository;

import com.po.app.Unit;
import com.po.app.WebAccess;
import com.po.app.data.gios.model.index.Index;
import com.po.app.data.gios.model.measuring_station.MeasuringStation;
import com.po.app.data.gios.model.sensor.Param;
import com.po.app.data.gios.model.sensor.Sensor;
import com.po.app.data.gios.model.sensor_measurements.Measurements;
import com.po.app.data.gios.model.sensor_measurements.Value;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.ws.rs.WebApplicationException;
import java.lang.reflect.Field;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static junit.framework.TestCase.fail;

@Category({Unit.class, WebAccess.class})
public class GiosDataSourceTest {

    private boolean checkNotNull(Object object, Set<String> fieldNames) throws IllegalAccessException {
        for (Field f : object.getClass().getDeclaredFields()) {
            if (fieldNames.contains(f.getName())) {
                f.setAccessible(true);
                if (f.get(object) == null) {
                    System.out.println(f.getName());
                    return false;
                } else {
                    //System.out.println(f.get(object));
                }
            }
        }
        return true;
    }

    @Test
    public void findAllStations() {
        GiosDataSource dataSource = new GiosDataSource();
        List<MeasuringStation> stations = dataSource.findAllStations();
        Set<String> nonNullFieldNames = new HashSet<String>() {{
            add("id");
            add("stationName");
        }};

        for (MeasuringStation station : stations) {
            try {
                boolean notNull = checkNotNull(station, nonNullFieldNames);
                if (!notNull) {
                    fail("Null fields in MeasuringStation.class instance!");
                }
            } catch (IllegalAccessException exc) {
                fail("Illegal access to MeasuringStation.class instance!");
            }
        }
    }

    @Test
    public void getSensors() {
        GiosDataSource dataSource = new GiosDataSource();
        List<Sensor> sensors = dataSource.getSensors(14); // ID MAY change if provider decides so.
        Set<String> nonNullFieldNames = new HashSet<String>() {{
            add("id");
            add("param");
        }};

        for (Sensor station : sensors) {
            try {
                boolean notNull = checkNotNull(station, nonNullFieldNames);
                if (!notNull) {
                    fail("Null fields in Sensor.class instance!");
                } else {
                    Param param = station.getParam();
                    nonNullFieldNames.add("idParam");
                    nonNullFieldNames.add("paramCode");
                    if (!checkNotNull(param, nonNullFieldNames)) {
                        fail("Null fields in Param.class instance!");
                    }
                }
            } catch (IllegalAccessException exc) {
                fail("Illegal access to Sensor.class instance!");
            }
        }

        try {
            dataSource.getSensors(-1);
            fail("This should result in a WebApplicationException!");
        } catch (WebApplicationException exc) {
            // This should fail!
        }
    }

    @Test
    public void getSensorData() {
        GiosDataSource dataSource = new GiosDataSource();
        Measurements measurements = dataSource.getSensorData(1);
        Set<String> nonNullFieldNames = new HashSet<String>() {{
            add("values");
        }};

        try {
            boolean notNull = checkNotNull(measurements, nonNullFieldNames);
            if (!notNull) {
                fail("Null fields in Measurements.class instance!");
            } else {
                Value[] values = measurements.getValues();
                nonNullFieldNames.add("date");
                nonNullFieldNames.add("value");
                for (Value v : values) {
                    if (!checkNotNull(v, nonNullFieldNames)) {
                        System.out.println("Null fields in Value.class instance!");
                        // WARNING: this just proves that values sometimes ARE null!!!
                        // Must be handled at higher levels.
                    }
                }
            }
        } catch (IllegalAccessException exc) {
            fail("Illegal access to Measurements.class instance!");
        }

        try {
            dataSource.getSensorData(-1);
            fail("This should result in a WebApplicationException!");
        } catch (WebApplicationException exc) {
            // This should fail!
        }
    }

    @Test
    public void getIndex() {
        GiosDataSource dataSource = new GiosDataSource();
        Index index = dataSource.getIndex(52);
        Set<String> nonNullFieldNames = new HashSet<String>() {{
            add("stCalcDate");
            add("stIndexLevel");
        }};

        try {
            boolean notNull = checkNotNull(index, nonNullFieldNames);
            if (!notNull) {
                fail("Null fields in Index.class instance!");
            }
        } catch (IllegalAccessException exc) {
            fail("Illegal access to Index.class instance!");
        }
    }
}