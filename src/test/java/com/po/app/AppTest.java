package com.po.app;

import static org.mockito.Mockito.*;

import com.po.app.data.airly.repository.AirlyDataSource;
import com.po.app.data.airly.AirlyService;
import com.po.app.data.gios.repository.GiosDataSource;
import com.po.app.data.gios.GiosService;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class AppTest {

    @Test
    public void jsonTest() throws IOException {
        String fileName = "/gios/get_sensors/sensors_14.json";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String fileContent = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));
        System.out.println(fileContent);
    }

    @Test
    public void testMockito() {
        // mock creation
        List mockedList = mock(List.class);

        // using mock object - it does not throw any "unexpected interaction" exception
        mockedList.add("one");
        mockedList.clear();

        // selective, explicit, highly readable verification
        verify(mockedList).add("one");
        verify(mockedList).clear();

        // stubbing appears before the actual execution
        when(mockedList.get(0)).thenReturn("first");

        // the following prints "first"
        System.out.println(mockedList.get(0));

        // the following prints "null" because get(999) was not stubbed
        System.out.println(mockedList.get(999));
    }

    @Test
    public void testGios() {
        GiosDataSource dataSource = new GiosDataSource();
        GiosService service = new GiosService(dataSource);
        System.out.println(dataSource.getIndex(-1));
        System.out.println(service.getParamValue(
                164,
                new ArrayList<LocalDateTime>() {{
                    add(LocalDateTime.of(2019, 1, 5, 10, 0));
                    add(LocalDateTime.of(2018, 12, 31, 14, 0));
                }},
                new ArrayList<String>() {{
                    add("NO2");
                    add("C6H6");
                }}));
        //System.out.println(service.getNameIdMap());
        System.out.println(dataSource.getSensors(1));
    }

    @Test
    public void testAirly() throws IOException {
        String fileName = "/credentials.txt";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String API_KEY = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));
        AirlyDataSource dataSource = new AirlyDataSource(API_KEY);
        AirlyService service = new AirlyService(dataSource);
        System.out.println(dataSource.findAllInstallations());
        //System.out.println(service.getCurrentIndex(576));
        //System.out.println(service.getCurrentParamValue(576, new ArrayList<String>() {{add("PM25");}}));
//        System.out.println(service.getIndex(6986,
//                new ArrayList<LocalDateTime>() {{
//                    add(LocalDateTime.of(2019, 1, 5, 4, 0));
//                }}));
//        System.out.println(service.getParamValue(6986,
//                new ArrayList<LocalDateTime>() {{
//                    add(LocalDateTime.of(2019, 1, 5, 4, 0));
//                }},
//                new ArrayList<String>() {{
//                }}));
    }
}
