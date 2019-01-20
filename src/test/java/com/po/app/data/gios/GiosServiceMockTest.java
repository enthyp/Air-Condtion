package com.po.app.data.gios;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.po.app.Unit;
import com.po.app.data.gios.model.index.Index;
import com.po.app.data.gios.model.measuring_station.MeasuringStation;
import com.po.app.data.gios.model.sensor.Sensor;
import com.po.app.data.gios.model.sensor_measurements.Measurements;
import com.po.app.data.gios.repository.GiosDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.ws.rs.ProcessingException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

@Category(Unit.class)
@RunWith(MockitoJUnitRunner.class)
public class GiosServiceMockTest {

    @Mock
    GiosDataSource dataSource;

    @InjectMocks
    GiosService service;

    @Test
    public void getNameIdMap() throws IOException {
        String fileName = "/gios/find_all/get_name_id_map_short_target.txt";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String target = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));
        assertEquals(target, service.getNameIdMap().toString());
    }

    @Test
    public void getCurrentIndex() throws IOException {
        String fileName = "/gios/get_index/get_current_index_target.txt";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String target = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));
        assertEquals(target, service.getCurrentIndex(52).toString());

        try {
            service.getCurrentIndex(-1);
            fail("This should result in a ProcessingException!");
        } catch (ProcessingException exc) {
            // This should fail :)
        }
    }

    @Test
    public void getIndex() throws IOException {
        String fileName = "/gios/get_index/get_index_target.txt";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String target = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));
        assertEquals(target, service.getIndex(52, new ArrayList<>()).toString());

        LocalDateTime dt = LocalDateTime.of(2018, 12, 29, 8, 20, 30);
        assertEquals(target, service.getIndex(52,
                new ArrayList<LocalDateTime>() {{
                    add(dt);
                    add(dt.minusDays(1));
        }}).toString());

        assertEquals("{}", service.getIndex(52,
                new ArrayList<LocalDateTime>() {{
                    add(dt.plusDays(1));
        }}).toString());

        try {
            service.getIndex(-1,
                    new ArrayList<LocalDateTime>() {{
                        add(dt.plusDays(1));
            }});
            fail("This should result in a ProcessingException!");
        } catch (ProcessingException exc) {
            // This should fail :)
        }
    }

    @Test
    public void getCurrentParamValue() throws IOException {
        try {
            service.getCurrentParamValue(14, new ArrayList<String>() {{
                add("NO2");
            }});
            fail("This should result in a ProcessingException!");
        } catch (ProcessingException exc) {
            // This should fail!
        }

        try {
            service.getCurrentParamValue(-1, new ArrayList<>());
            fail("This should result in a ProcessingException!");
        } catch (ProcessingException exc) {
            // This should fail!
        }

        String fileName = "/gios/getParamValue/get_current_param_value_target_14.txt";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String target = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));
        assertEquals(target, service.getCurrentParamValue(14, new ArrayList<>()).toString());
    }

    @Test
    public void getParamValue() throws IOException {
        try {
            service.getParamValue(14,
                    new ArrayList<LocalDateTime>() {{
                        LocalDateTime.now();
                    }},
                    new ArrayList<String>() {{
                        add("SO2");
                    }});
            fail("This should result in a ProcessingException!");
        } catch (ProcessingException exc) {
            // This should fail!
        }

        try {
            service.getParamValue(-1,
                    new ArrayList<LocalDateTime>() {{
                        LocalDateTime.now();
                    }},
                    new ArrayList<>());
            fail("This should result in a ProcessingException!");
        } catch (ProcessingException exc) {
            // This should fail!
        }

        String fileName = "/gios/getParamValue/get_param_value_target_14.txt";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String target = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));
        assertEquals(target, service.getParamValue(14,
                new ArrayList<LocalDateTime>() {{
                    add(LocalDateTime.parse("2018-12-29T07:00:00"));
                }},
                new ArrayList<String>() {{
                    add("PM10");
                }}).toString());
    }

    @Before
    public void setupFindAll() throws IOException {
        String fileName = "/gios/find_all/find_all_short.json";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String json = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        MeasuringStation[] stations = new ObjectMapper().readValue(json, MeasuringStation[].class);
        doReturn(new ArrayList<>(Arrays.asList(stations))).when(dataSource).findAllStations();
    }

    @Before
    public void setupGetIndex() throws IOException {
        String fileName = "/gios/get_index/index_52.json";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String json = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        Index index = new ObjectMapper().readValue(json, Index.class);
        doReturn(index).when(dataSource).getIndex(52);
        doReturn(new Index()).when(dataSource).getIndex(not(eq(52)));
    }

    @Before
    public void setupGetSensors() throws IOException {
        String fileName = "/gios/get_sensors/sensors_14.json";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String json = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        Sensor[] sensors = new ObjectMapper().readValue(json, Sensor[].class);
        doReturn(new ArrayList<>(Arrays.asList(sensors))).when(dataSource).getSensors(14);
        doReturn(new ArrayList<>()).when(dataSource).getSensors(not(eq(14)));
    }

    @Before
    public void setupGetSensorData() throws IOException {
        String fileName = "/gios/get_sensors_data/data_92.json";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String json = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));

        Measurements measurements = new ObjectMapper().readValue(json, Measurements.class);
        doReturn(measurements).when(dataSource).getSensorData(92);
        doReturn(new Measurements()).when(dataSource).getSensorData(not(eq(92)));
    }
}