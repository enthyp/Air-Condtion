package com.po.app.data.gios;

import com.po.app.Integration;
import com.po.app.WebAccess;
import com.po.app.data.gios.repository.GiosDataSource;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.*;

@Category({Integration.class, WebAccess.class})
public class GiosServiceTest {
    @Test
    public void getNameIdMap() {
        GiosDataSource dataSource = new GiosDataSource();
        GiosService service = new GiosService(dataSource);
        service.getNameIdMap();
    }

    @Test
    public void getCurrentIndex() {
        GiosDataSource dataSource = new GiosDataSource();
        GiosService service = new GiosService(dataSource);
        service.getCurrentIndex(52);

        try {
            service.getCurrentIndex(   -1);
            fail("This should result in a ProcessingException!");
        } catch (ProcessingException exc) {
            // This should fail :)
        }
    }

    @Test
    public void getIndex() {
        GiosDataSource dataSource = new GiosDataSource();
        GiosService service = new GiosService(dataSource);
        service.getIndex(52, new ArrayList<LocalDateTime>() {{
            add(LocalDateTime.parse("2019-01-20T21:00:00"));
        }});

        try {
            service.getIndex(   -1, new ArrayList<>());
            fail("This should result in a ProcessingException!");
        } catch (ProcessingException exc) {
            // This should fail :)
        }
    }

    @Test
    public void getCurrentParamValue() {
        GiosDataSource dataSource = new GiosDataSource();
        GiosService service = new GiosService(dataSource);
        service.getCurrentParamValue(52, new ArrayList<String>() {{
            add("PM10");
            add("PM@#@#");
        }});

        try {
            service.getCurrentParamValue(   -1, new ArrayList<>());
            fail("This should result in a WebApplicationException!");
        } catch (WebApplicationException exc) {
            // This should fail :)
        }

        try {
            service.getCurrentParamValue(52, new ArrayList<String>() {{
                add("PM11110");
                add("PM@#@#");
            }});
        } catch (ProcessingException exc) {
            // Should it fail..?
        }
    }

    @Test
    public void getParamValue() {
        GiosDataSource dataSource = new GiosDataSource();
        GiosService service = new GiosService(dataSource);
        service.getParamValue(52,
                new ArrayList<>(),
                new ArrayList<String>() {{
                    add("PM10");
                    add("PM@#@#");}});

        try {
            service.getParamValue(-1,
                    new ArrayList<>(),
                    new ArrayList<String>() {{
                        add("PM10");
                        add("PM@#@#");}});
            fail("This should result in a WebApplicationException!");
        } catch (WebApplicationException exc) {
            // This should fail :)
        }

        try {
            service.getParamValue(52,
                    new ArrayList<>(),
                    new ArrayList<String>() {{
                        add("P2M10");
                        add("PM@#@#");}});
        } catch (ProcessingException exc) {
            // Should it fail..?
        }
    }
}