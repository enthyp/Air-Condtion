package com.po.app.data.gios.repository;

import com.po.app.Unit;
import com.po.app.WebAccess;
import com.po.app.data.gios.model.measuring_station.MeasuringStation;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

@Category({Unit.class, WebAccess.class})
public class GiosDataSourceTest {

    private boolean checkNotNull(Object object) throws IllegalAccessException {
        for (Field f : object.getClass().getDeclaredFields()) {
            f.setAccessible(true);
            if (f.get(object) == null) {
                System.out.println(f.getName());
                return false;
            }
        }
        return true;
    }

    @Test
    public void findAllStations() throws IOException {
        String fileName = "/gios/find_all/findAll.json";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String target = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));
        GiosDataSource dataSource = new GiosDataSource();
        List<MeasuringStation> stations = dataSource.findAllStations();

        for (MeasuringStation station : stations) {
            try {
                boolean notNull = checkNotNull(station);
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
    }

    @Test
    public void getSensorData() {
    }

    @Test
    public void getIndex() {
    }
}