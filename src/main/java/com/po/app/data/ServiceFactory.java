package com.po.app.data;

import com.po.app.App;
import com.po.app.Command;
import com.po.app.data.airly.AirlyService;
import com.po.app.data.airly.repository.AirlyCachedDataSource;
import com.po.app.data.airly.repository.AirlyDataSource;
import com.po.app.data.airly.repository.IAirlyDataSource;
import com.po.app.data.gios.GiosService;
import com.po.app.data.gios.repository.GiosCachedDataSource;
import com.po.app.data.gios.repository.GiosDataSource;
import com.po.app.data.gios.repository.IGiosDataSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.stream.Collectors;

public class ServiceFactory {
    public static IService getService(Command.DataProvider provider, boolean useCaching) {
        IService service;

        if (provider.equals(Command.DataProvider.GIOS)) {
            IGiosDataSource dataSource = new GiosDataSource();

            if (useCaching) {
                try {
                    dataSource = new GiosCachedDataSource(dataSource);
                } catch (IOException exc) {
                    System.out.println(exc.getMessage());
                }
            }

            service = new GiosService(dataSource);
            return service;
        } else if (provider.equals(Command.DataProvider.Airly)) {
            try {
                String fileName = "/credentials.txt";
                File file = new File(App.class.getResource(fileName).getFile());
                String API_KEY = Files.lines(file.toPath(), StandardCharsets.UTF_8)
                        .collect(Collectors.joining("\n"));
                IAirlyDataSource dataSource = new AirlyDataSource(API_KEY);

                if (useCaching) {
                    try {
                        System.out.println("WARNING: Airly caching is currently not supported!");
                        new AirlyCachedDataSource(dataSource);
                    } catch (IOException exc) {
                        System.out.println(exc.getMessage());
                    }
                }

                service = new AirlyService(dataSource);
                return service;
            } catch (IOException exc) {
                System.out.println("Could not retrieve API key for Airly client!");
            }
        }

        return null;
    }
}
