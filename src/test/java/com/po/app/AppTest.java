package com.po.app;

import static org.mockito.Mockito.*;

import com.po.app.data.airly.AirlyDataSource;
import com.po.app.data.gios.GiosDataSource;
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
        String fileName = "/sensors_14.json";
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
        System.out.println(service.getParamValue(
                164,
                new ArrayList<LocalDateTime>() {{
                    add(LocalDateTime.of(2018, 12, 31, 16, 0));
                    add(LocalDateTime.of(2018, 12, 31, 14, 0));
                }},
                new ArrayList<String>() {{
                    add("NO2");
                    add("C6H6");
                }}));
    }

    @Test
    public void testAirly() throws IOException {
        String fileName = "/credentials.txt";
        File file = new File(this.getClass().getResource(fileName).getFile());
        String API_KEY = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                .collect(Collectors.joining("\n"));
        System.out.println(API_KEY);
        AirlyDataSource dataSource = new AirlyDataSource(API_KEY);
    }
}
