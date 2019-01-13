package com.po.app;

import static org.mockito.Mockito.*;

import com.po.app.data.airly.repository.AirlyDataSource;
import com.po.app.data.airly.AirlyService;
import com.po.app.data.gios.repository.GiosCachedDataSource;
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

    /*
     * TODO: for each method of each datasource - test for:
     * -> incorrect (e.g. negative)/empty argument values (what are the results? Null values? Exceptions?)
     * -> handling HTTP404, HTTP500 and similar
     */

    @Test
    public void mainTestCommandLine() {
        //App.main(new String[] {"-d", "GIOS", "-f3", "noob", "GIOS", "2018-12-22_12:04:00", "2018-12-22_12:04:00"});
        //App.main(new String[] {"-d", "GIOS", "-f4", "noob", "doob-meyer", "2018-12-12_12:04:00"});
        //App.main(new String[] {"-d", "GIOS", "-f1", "Kraków, Aleja Karasińskiego"});
        //App.main(new String[] {"-d", "GIOS", "-f2", "Kraków, Aleja Krasińskiego", "PM10"});
//        App.main(new String[] {"-d", "GIOS", "-c", "-f3", "Kraków, Aleja Krasińskiego", "PM10",
//                "2018-12-12_12:04:00", "2019-12-12_12:04:00"});
        App.main(new String[] {"-d", "Airly", "-f4", "Osiedle Dywizjonu 303", "Kraków-Nowa Huta", "2018-12-22_12:04:00"});


    }

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
    public void testGios() throws IOException {
        GiosDataSource dataSource = new GiosDataSource();
        GiosCachedDataSource cachedDataSource = new GiosCachedDataSource(dataSource);
        GiosService service = new GiosService(cachedDataSource);
        System.out.println(dataSource.getIndex(-1));
//        System.out.println(service.getParamValue(
//                164,
//                new ArrayList<LocalDateTime>() {{
//                    add(LocalDateTime.of(2019, 1, 5, 10, 0));
//                    add(LocalDateTime.of(2018, 12, 31, 14, 0));
//                }},
//                new ArrayList<String>() {{
//                    add("NO2");
//                    add("C6H6");
//                }}));
//        System.out.println(service.getNameIdMap());
        //System.out.println(cachedDataSource.findAllStations());
        //System.out.println(cachedDataSource.getSensors(14));
        //System.out.println(cachedDataSource.getSensorData(92));
        //System.out.println(cachedDataSource.getIndex(52));
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
