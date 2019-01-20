package com.po.app.data.airly;

import com.po.app.Integration;
import com.po.app.Unit;
import com.po.app.WebAccess;
import com.po.app.data.airly.repository.AirlyDataSource;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.ws.rs.WebApplicationException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Properties;

import static junit.framework.TestCase.fail;

@Category({Integration.class, WebAccess.class})
public class AirlyServiceTest {

    @Test
    public void getNameIdMap() {
    }

    @Test
    public void getCurrentIndex() {
    }

    @Test
    public void getIndex() {
    }

    @Test
    public void getCurrentParamValue() {
    }

    @Test
    public void getParamValue() {
    }

    @Test
    public void testAirly() throws IOException {
        String resourceName = "cred.properties";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties credProps = new Properties();
        try(InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            credProps.load(resourceStream);
        }
        String API_KEY = credProps.getProperty("API_KEY");
        AirlyDataSource dataSource = new AirlyDataSource(API_KEY);
        AirlyService service = new AirlyService(dataSource);

        dataSource.findAllInstallations();
        service.getCurrentIndex(576);
        service.getCurrentParamValue(576, new ArrayList<String>() {{add("PM25");}});


        service.getIndex(6986,
                new ArrayList<LocalDateTime>() {{
                    add(LocalDateTime.of(2019, 1, 5, 4, 0));
                }});
        try {
            service.getIndex(0,
                    new ArrayList<LocalDateTime>() {{
                        add(LocalDateTime.of(2019, 1, 5, 4, 0));
                    }});
            fail("This should result in a WebApplicationException!");
        } catch (WebApplicationException exc) {
            // This should fail.
        }

        service.getParamValue(6986,
                new ArrayList<LocalDateTime>() {{
                    add(LocalDateTime.of(2019, 1, 5, 4, 0));
                }},
                new ArrayList<String>() {{
                }});
        try {
            service.getParamValue(0,
                    new ArrayList<LocalDateTime>() {{
                        add(LocalDateTime.of(2019, 1, 5, 4, 0));
                    }},
                    new ArrayList<String>() {{
                    }});
            fail("This should result in a WebApplicationException!");
        } catch (WebApplicationException exc) {
            // This should fail.
        }
    }
}