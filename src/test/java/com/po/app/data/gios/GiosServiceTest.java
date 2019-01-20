package com.po.app.data.gios;

import static org.junit.Assert.assertEquals;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.doReturn;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.po.app.Integration;
import com.po.app.Unit;
import com.po.app.data.gios.model.index.Index;
import com.po.app.data.gios.model.measuring_station.MeasuringStation;
import com.po.app.data.gios.repository.GiosDataSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

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
public class GiosServiceTest {

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
    }

    @Test
    public void getCurrentParamValue() throws IOException {

//        String fileName = "/gios/get_index/get_index_target.txt";
//        File file = new File(this.getClass().getResource(fileName).getFile());
//        String target = Files.lines(file.toPath(), StandardCharsets.UTF_8)
//                .collect(Collectors.joining("\n"));
//        assertEquals(target, service.getIndex(52, new ArrayList<>()).toString());
    }

    @Test
    public void getParamValue() {
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
//        doReturn(new Index()).when(dataSource).getIndex(not(eq(52)));
    }

//    @Before
//    public void setupGetSensors() throws IOException {
//        String fileName = "/gios/get_sensors/sensors_14.json";
//        File file = new File(this.getClass().getResource(fileName).getFile());
//        String json = Files.lines(file.toPath(), StandardCharsets.UTF_8)
//                .collect(Collectors.joining("\n"));
//
//        Sensor[] sensors = new ObjectMapper().readValue(json, Sensor[].class);
//        doReturn(new ArrayList<>(Arrays.asList(sensors))).when(dataSource).getSensors(14);
//        doReturn(new ArrayList<>()).when(dataSource).getSensors(not(eq(14)));
//    }
//
//    @Before
//    public void setupGetSensorData() throws IOException {
//        String fileName = "/gios/get_sensors_data/data_92.json";
//        File file = new File(this.getClass().getResource(fileName).getFile());
//        String json = Files.lines(file.toPath(), StandardCharsets.UTF_8)
//                .collect(Collectors.joining("\n"));
//
//        Measurements measurements = new ObjectMapper().readValue(json, Measurements.class);
//        doReturn(measurements).when(dataSource).getSensorData(92);
//        doReturn(new Measurements()).when(dataSource).getSensorData(not(eq(92)));
//    }
}