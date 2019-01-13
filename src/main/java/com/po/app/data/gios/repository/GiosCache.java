package com.po.app.data.gios.repository;

import com.po.app.data.gios.model.index.Index;
import com.po.app.data.gios.model.measuring_station.MeasuringStation;
import com.po.app.data.gios.model.sensor.Sensor;
import com.po.app.data.gios.model.sensor_measurements.Measurements;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static java.nio.file.StandardOpenOption.CREATE;

public class GiosCache implements IGiosCache {

    private final Path basePath = Paths.get(System.getProperty("user.home"), ".air_cond_cache", "gios");
    private final DateTimeFormatter formatter;


    public GiosCache() throws IOException {
        // Create cache directory if it's not in place yet.
        if (Files.exists(basePath)) {
            if (!Files.isDirectory(basePath)) {
                throw new IOException("Could not create cache directory under " + basePath);
            }
        } else {
            Files.createDirectories(basePath);
        }

        // Set formatter to name cache files appropriately.
        this.formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd__HH_mm_ss");
    }

    @Override
    public List<MeasuringStation> findAllStations() throws IOException {
        @SuppressWarnings("unchecked")
        List<MeasuringStation> stations = (List<MeasuringStation>)this.getCacheContent("all_stations");

        return stations;
    }

    @Override
    public void setAllStations(List<MeasuringStation> stations) throws IOException {
        this.setCacheContent("all_stations", stations);
    }

    @Override
    public List<Sensor> getSensors(int stationId) throws IOException {
        @SuppressWarnings("unchecked")
        List<Sensor> sensors = (List<Sensor>) this.getCacheContent("sensors_station" + stationId);

        return sensors;
    }

    @Override
    public void setSensors(List<Sensor> sensors, int stationId) throws IOException {
        this.setCacheContent("sensors_station" + stationId, sensors);
    }

    @Override
    public Measurements getSensorData(int sensorId) throws IOException {
        @SuppressWarnings("unchecked")
        Measurements measurements = (Measurements) this.getCacheContent("sensor_data" + sensorId);

        return measurements;
    }

    @Override
    public void setSensorData(Measurements measurements, int sensorId) throws IOException {
        this.setCacheContent("sensor_data" + sensorId, measurements);
    }

    @Override
    public Index getIndex(int stationId) throws IOException {
        @SuppressWarnings("unchecked")
        Index index = (Index) this.getCacheContent("index" + stationId);

        return index;
    }

    @Override
    public void setIndex(Index index, int stationId) throws IOException {
        this.setCacheContent("index" + stationId, index);
    }


    private Object getCacheContent(String fileNamePattern) throws IOException {
        // Look for up-to-date file in the cache.
        FileFinder finder = new FileFinder(fileNamePattern);
        Files.walkFileTree(this.basePath, new HashSet<>(), 1, finder);

        if (finder.found) {
            Path sensorsPath = finder.targetFilePath;

            // Deserialize cache file's contents.
            try (ObjectInputStream inputStream = new ObjectInputStream(Files.newInputStream(sensorsPath))) {
                try {
                    @SuppressWarnings("unchecked")
                    Object cacheContent = inputStream.readObject();
                    System.out.println("Loaded content from cache.");
                    return cacheContent;
                } catch (ClassNotFoundException exc) {
                    throw new IOException("Could not deserialize cache file!" + exc.getMessage());
                }
            }
        }

        return null;
    }

    private void setCacheContent(String fileNamePattern, Object content) throws IOException {
        if (content instanceof Serializable) {
            LocalDateTime current = LocalDateTime.now();
            Path cacheFilePath = basePath.resolve(fileNamePattern + current.format(this.formatter));

            try (ObjectOutputStream outputStream =
                         new ObjectOutputStream(Files.newOutputStream(cacheFilePath, CREATE))) {
                outputStream.writeObject(content);
                System.out.println("Saved content to cache.");
            }
        } else {
            throw new IOException("Content not serializable!");
        }
    }

    private class FileFinder extends SimpleFileVisitor<Path> {

        private final String pattern;
        private final PathMatcher matcher;
        private Path targetFilePath;
        private boolean found;
        private List<Path> toBeRemoved = new ArrayList<>();

        FileFinder(String pattern) {
            this.pattern = pattern;
            this.matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern + "*");
            this.found = false;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Path fileName = file.getFileName();

            if (this.matcher.matches(fileName)) {
                String name = fileName.toString();
                String dateTimeString = name.replaceFirst("^" + pattern, "");
                try {
                    LocalDateTime current = LocalDateTime.now();
                    LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

                    if (Duration.between(dateTime, current).toMinutes() < 10) {
                        this.targetFilePath = file;
                        this.found = true;
                    } else {
                        toBeRemoved.add(file);
                    }
                } catch (DateTimeException exc) {
                    System.out.println("Unexpected cache file format: " + exc.getMessage());
                    // Do nothing :)))
                }
            }

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            // Delete all out-of-date cache files encountered during the search.
            for (Path file : toBeRemoved)
                Files.delete(file);
            return FileVisitResult.CONTINUE;
        }
    }
}
