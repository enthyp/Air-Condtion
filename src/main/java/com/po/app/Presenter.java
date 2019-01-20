package com.po.app;

import com.po.app.data.IService;
import com.po.app.data.ServiceFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import java.time.LocalDateTime;
import java.util.*;

public class Presenter {

    private Command cmd;
    private IService service;
    private Command.DataProvider provider;

    public Presenter(Command cmd) {
        this.cmd = cmd;
    }

    public void run() {
        if (cmd.parsed()) {
            this.provider = cmd.getProvider();
            boolean useCaching = cmd.useCaching();
            this.service = ServiceFactory.getService(this.provider, useCaching);
            // TODO: too many places must be changed to add functionality (vide: lecture)!
            switch (cmd.getFunctionality()) {
                case s: show();
                    break;
                case f1: functionality1();
                    break;
                case f2: functionality2();
                    break;
                case f3: functionality3();
                    break;
                case f4: functionality4();
                    break;
                case f5: functionality5();
                    break;
                case f6: functionality6();
                    break;
                case f7: functionality7();
                    break;
                case f8: functionality8();
            }
        }
    }

    private void show() {
        try {
            Map<String, Integer> nameIdMap = this.service.getNameIdMap();
            System.out.println("Stations:");
            nameIdMap.keySet().stream()
                    .sorted()
                    .map(s -> "-> " + s)
                    .forEach(System.out::println);
        } catch (ProcessingException exc) {
            System.out.println("Processing issue: " + exc.getMessage());
        } catch (WebApplicationException exc) {
            System.out.println("Server issue occurred: " + exc.getMessage());
        }
    }

    private void functionality1() {
        String stationName = cmd.getStationNames().get(0);
        try {
            Map<String, Integer> nameIdMap = this.service.getNameIdMap();

            int id = this.getStationId(nameIdMap, stationName, this.provider);
            if (id == -1) {
                System.out.println("Station not found!");
            } else {
                try {
                    IService.StringInstant index = this.service.getCurrentIndex(id);

                    if (index != null && index.dateTime != null && index.content != null) {
                        System.out.println(String.format("Index value for %s: %s", index.dateTime, index.content));
                    } else {
                        System.out.println("Empty response!");
                    }
                } catch (WebApplicationException exc) {
                    System.out.println(exc.getMessage());
                }
            }
        } catch (WebApplicationException | ProcessingException exc) {
            System.out.println(exc.getMessage());
        }
    }

    private void functionality2() {
        String stationName = cmd.getStationNames().get(0);
        String parameterName = cmd.getParameterNames().get(0);

        try {
            Map<String, Integer> nameIdMap = this.service.getNameIdMap();
            int id = this.getStationId(nameIdMap, stationName, this.provider);

            try {
                Map<String, IService.DoubleInstant> paramValues =
                        this.service.getCurrentParamValue(id, new ArrayList<String>() {{
                            add(parameterName);
                        }});

                if (paramValues.containsKey(parameterName)) {
                    IService.DoubleInstant value = paramValues.get(parameterName);
                    if (value.dateTime != null) {
                        System.out.println(String.format("Value for parameter %s at %s: %f",
                                parameterName, value.dateTime, value.value));
                    } else {
                        System.out.println("Empty response!");
                    }
                } else {
                    if (id == -1)
                        System.out.println("Station not found");
                    else
                        System.out.println("No data for this parameter!");
                }
            } catch (ProcessingException | WebApplicationException exc) {
                System.out.println(exc.getMessage());
            }
        } catch (ProcessingException | WebApplicationException exc) {
            System.out.println(exc.getMessage());
        }
    }

    private void functionality3() {
        String stationName = cmd.getStationNames().get(0);
        String parameterName = cmd.getParameterNames().get(0);
        LocalDateTime dateFrom = cmd.getDateTimes().get(0);
        LocalDateTime dateTo = cmd.getDateTimes().get(1);

        try {
            Map<String, Integer> nameIdMap = this.service.getNameIdMap();
            int id = this.getStationId(nameIdMap, stationName, this.provider);

            try {
                Map<IService.StringInstant, Double> paramValues =
                        this.service.getParamValue(id, new ArrayList<>(), new ArrayList<String>() {{
                            add(parameterName);
                        }});

                if (!paramValues.isEmpty()) {
                    int count = 0;
                    double average = 0;

                    for (Map.Entry<IService.StringInstant, Double> entry : paramValues.entrySet()) {
                        IService.StringInstant param = entry.getKey();

                        if (param.content.equals(parameterName) && param.dateTime != null) {
                            if ((param.dateTime.isBefore(dateTo) || param.dateTime.isEqual(dateTo)) &&
                                    (param.dateTime.isAfter(dateFrom) || param.dateTime.isEqual(dateFrom))) {
                                average += entry.getValue();
                                count++;
                            }
                        }
                    }

                    if (count > 0) {
                        average /= count;
                        System.out.println(String.format("Average value for parameter %s between " +
                                "%s and %s: %f", parameterName, dateFrom, dateTo, average));
                    } else {
                        System.out.println("No measurements for this parameter and period combination!");
                    }
                } else {
                    if (id == -1)
                        System.out.println("Station not found!");
                    else
                        System.out.println("No measurements for this parameter and period combination!");
                }

            } catch (WebApplicationException exc) {
                System.out.println(exc.getMessage());
            }
        } catch (ProcessingException | WebApplicationException exc) {
            System.out.println(exc.getMessage());
        }
    }

    private void functionality4() {
        List<String> stationNames = cmd.getStationNames();
        LocalDateTime dateFrom = cmd.getDateTimes().get(0);

        // Get IDs of given stations.
        try {
            Map<String, Integer> nameIdMap = this.service.getNameIdMap();
            Map<String, Integer> nameIdMapSelected = new HashMap<>();
            for (String stationName : stationNames) {
                int id = this.getStationId(nameIdMap, stationName, this.provider);

                if (id != -1)
                    nameIdMapSelected.put(stationName, id);
                else
                    System.out.println(String.format("Station %s not found!", stationName));
            }

            // For each station find maximum variation.
            for (Map.Entry<String, Integer> entry : nameIdMapSelected.entrySet()) {
                String stationName = entry.getKey();
                try {
                    Map<IService.StringInstant, Double> paramValues =
                            this.service.getParamValue(entry.getValue(), new ArrayList<>(), new ArrayList<>());

                    // First add all parameter measurements to parameter name -> List<(time instant, value)> map.
                    Map<String, List<IService.DoubleInstant>> paramValuesSorted = new HashMap<>();
                    for (IService.StringInstant stringInstant : paramValues.keySet()) {
                        String parameterName = stringInstant.content;
                        LocalDateTime dateTime = stringInstant.dateTime;
                        double value = paramValues.get(stringInstant);

                        if (parameterName != null && dateTime != null &&
                                (dateTime.isAfter(dateFrom) || dateTime.isEqual(dateFrom))) {
                            if (!paramValuesSorted.containsKey(parameterName)) {
                                paramValuesSorted.put(parameterName, new ArrayList<>());
                            }
                            paramValuesSorted.get(parameterName)
                                    .add(new IService.DoubleInstant(dateTime, value));
                        }
                    }

                    // Next, sort each parameter values list according to date.
                    for (String parName : paramValuesSorted.keySet())
                        paramValuesSorted.get(parName).sort(Comparator.comparing(IService.DoubleInstant::getDateTime));

                    // Next, calculate variations.
                    double maxVariation = 0;
                    String maxVarPar = "";

                    for (Map.Entry<String, List<IService.DoubleInstant>> subEntry : paramValuesSorted.entrySet()) {
                        String parName = subEntry.getKey();
                        List<IService.DoubleInstant> measurements = subEntry.getValue();

                        double variation = 0;
                        if (measurements.size() > 1) {
                            for (int i = 1; i < measurements.size(); i++) {
                                variation += Math.abs(measurements.get(i).value - measurements.get(i - 1).value);
                            }
                        }

                        if (variation > maxVariation) {
                            maxVariation = variation;
                            maxVarPar = parName;
                        }
                    }

                    if (!maxVarPar.equals("")) {
                        System.out.println(String.format("Station %s - greatest variation for parameter %s, of value %f",
                                stationName, maxVarPar, maxVariation));
                    }
                } catch (WebApplicationException | ProcessingException exc) {
                    System.out.println(exc.getMessage());
                }
            }
        } catch (WebApplicationException | ProcessingException exc) {
            System.out.println(exc.getMessage());
        }
    }

    private void functionality5() {
        LocalDateTime datetime = cmd.getDateTimes().get(0);
        try {
            Map<String, Integer> nameIdMap = this.service.getNameIdMap();
            String stationName = cmd.getStationNames().get(0);
            int stationId = this.getStationId(nameIdMap, stationName, this.provider);

            boolean minValueFound = false;
            String minParameterName = "";
            double minValue = 0;

            if (stationId != -1) {
                try {
                    Map<IService.StringInstant, Double> paramValues =
                            this.service.getParamValue(
                                    stationId,
                                    new ArrayList<LocalDateTime>() {{
                                        add(datetime);
                                    }},
                                    new ArrayList<>());

                    for (Map.Entry<IService.StringInstant, Double> measurement : paramValues.entrySet()) {
                        String parameterName = measurement.getKey().content;
                        double value = measurement.getValue();

                        if (!minValueFound || value < minValue) {
                            if (!minValueFound)
                                minValueFound = true;
                            minParameterName = parameterName;
                            minValue = value;
                        }
                    }

                    if (!minValueFound) {
                        // TODO: change to returning Strings in all functionality methods
                        // TODO: maybe encapsulate functionality to reduce code duplication
                        System.out.println("No measurements found for given date!");
                    } else {
                        System.out.println(String.format("Minimal value %.2f of parameter %s found.",
                                minValue, minParameterName));
                    }
                } catch (WebApplicationException | ProcessingException exc) {
                    System.out.println(exc.getMessage());
                }
            } else {
                System.out.println("Station not found!");
            }
        } catch (WebApplicationException | ProcessingException exc) {
            System.out.println(exc.getMessage());
        }
    }

    private class ParamValue implements Comparable<ParamValue>{
        String parName;
        double parValue;

        ParamValue(String parName, double parValue) {
            this.parName = parName;
            this.parValue = parValue;
        }

        @Override
        public int compareTo(ParamValue other) {
            return Double.compare(this.parValue, other.parValue);
        }
    }

    private void functionality6() {
        LocalDateTime datetime = cmd.getDateTimes().get(0);
        try {
            Map<String, Integer> nameIdMap = this.service.getNameIdMap();
            String stationName = cmd.getStationNames().get(0);
            int stationId = this.getStationId(nameIdMap, stationName, this.provider);
            int N = this.cmd.getN();

            if (stationId != -1) {
                try {
                    Map<IService.StringInstant, Double> paramValues =
                            this.service.getParamValue(
                                    stationId,
                                    new ArrayList<LocalDateTime>() {{
                                        add(datetime);
                                    }},
                                    new ArrayList<>());

                    PriorityQueue<ParamValue> topNormBreakers = new PriorityQueue<>(Collections.reverseOrder());

                    for (Map.Entry<IService.StringInstant, Double> entry : paramValues.entrySet()) {
                        // TODO: make sure entries are never  empty! (with null key or sth)
                        String parName = entry.getKey().content;
                        double parValue = entry.getValue();

                        if (airQualityNorms.containsKey(parName)) {
                            double norm = airQualityNorms.get(parName);
                            if (parValue > norm) {
                                topNormBreakers.offer(new ParamValue(parName, parValue - norm));
                            }
                        }
                    }

                    if (!topNormBreakers.isEmpty()) {
                        for (int i = 0; i < N; i++) {
                            ParamValue paramValue = topNormBreakers.poll();
                            if (paramValue != null) {
                                System.out.println(String.format("%d. Parameter %s: %.2f norm exceeding.",
                                        i + 1, paramValue.parName, paramValue.parValue));
                            } else {
                                System.out.println(String.format("Only %d norm exceeding case%s found. ",
                                        i, i == 1 ? "" : "s"));
                                break;
                            }
                        }
                    } else {
                        System.out.println("No norm exceeding cases found!");
                    }
                } catch (WebApplicationException | ProcessingException exc) {
                    System.out.println(exc.getMessage());
                }
            } else {
                System.out.println("Station not found!");
            }

        } catch (WebApplicationException | ProcessingException exc) {
            System.out.println(exc.getMessage());
        }
    }

    private void functionality7() {
        String targetParName = cmd.getParameterNames().get(0);
        List<String> stationNames = cmd.getStationNames();

        // Get IDs of given stations.
        try {
            Map<String, Integer> nameIdMap = this.service.getNameIdMap();
            Map<String, Integer> nameIdMapSelected = new HashMap<>();
            for (String stationName : stationNames) {
                int id = this.getStationId(nameIdMap, stationName, this.provider);

                if (id != -1)
                    nameIdMapSelected.put(stationName, id);
                else
                    System.out.println(String.format("Station %s not found!", stationName));
            }

            boolean valuesFound = false;
            double minValue = 0, maxValue = 0;
            String minStationName = "", maxStationName = "";
            LocalDateTime minDateTime = LocalDateTime.now(), maxDateTime = LocalDateTime.now();

            for (Map.Entry<String, Integer> entry : nameIdMapSelected.entrySet()) {
                String stationName = entry.getKey();
                try {
                    Map<IService.StringInstant, Double> paramValues =
                            this.service.getParamValue(entry.getValue(), new ArrayList<>(),
                                    new ArrayList<String>() {{
                                        add(targetParName);
                                    }});

                    // TODO: add method to get values of one parameter from such map as above!
                    for (Map.Entry<IService.StringInstant, Double> parValue : paramValues.entrySet()) {
                        LocalDateTime dateTime = parValue.getKey().dateTime;
                        double value = parValue.getValue();

                        if (!valuesFound) {
                            minValue = maxValue = value;
                            minDateTime = maxDateTime = dateTime;
                            minStationName = maxStationName = stationName;
                        } else {
                            if (value < minValue) {
                                minValue = value;
                                minDateTime = dateTime;
                                minStationName = stationName;
                            } else if (value > maxValue) {
                                maxValue = value;
                                maxDateTime = dateTime;
                                maxStationName = stationName;
                            }
                        }

                        valuesFound = true;
                    }
                } catch (WebApplicationException | ProcessingException exc) {
                    System.out.println(exc.getMessage());
                }
            }

            if (valuesFound) {
                System.out.println(String.format("Minimum value: %.2f at '%s' station on %s" +
                                "\nMaximum value: %.2f at '%s' station on %s",
                        minValue, minStationName, minDateTime,
                        maxValue, maxStationName, maxDateTime));
            } else {
                System.out.println("No measurements found for given parameter!");
            }
        } catch (WebApplicationException | ProcessingException exc) {
            System.out.println(exc.getMessage());
        }
    }

    private void functionality8() {
        StringBuilder builder = new StringBuilder();

        // TODO: you just copied that again!!!!!!!!!
        String targetParName = cmd.getParameterNames().get(0);
        LocalDateTime dateFrom = cmd.getDateTimes().get(0);
        LocalDateTime dateTo = cmd.getDateTimes().get(1);
        List<String> stationNames = cmd.getStationNames();

        // Get IDs of given stations.
        try {
            Map<String, Integer> nameIdMap = this.service.getNameIdMap();
            Map<String, Integer> nameIdMapSelected = new HashMap<>();
            for (String stationName : stationNames) {
                int id = this.getStationId(nameIdMap, stationName, this.provider);

                if (id != -1)
                    nameIdMapSelected.put(stationName, id);
                else
                    System.out.println(String.format("Station %s not found!", stationName));
            }

            Map<String, List<IService.DoubleInstant>> stationMeasurementsMap = new HashMap<>();
            boolean valueFound = false;
            double maxValue = 0;

            for (String stationName : nameIdMapSelected.keySet()) {
                try {
                    Map<IService.StringInstant, Double> paramValues =
                            this.service.getParamValue(nameIdMapSelected.get(stationName),
                                    new ArrayList<>(),
                                    new ArrayList<String>() {{
                                        add(targetParName);
                                    }});

                    for (Map.Entry<IService.StringInstant, Double> parValue : paramValues.entrySet()) {
                        // TODO: again, providers must either provide non-empty objects or throw exceptions!
                        LocalDateTime dateTime = parValue.getKey().dateTime;
                        double value = parValue.getValue();

                        if ((dateTime.isEqual(dateFrom) || dateTime.isAfter(dateFrom))
                                && (dateTime.isEqual(dateTo) || dateTime.isBefore(dateTo))) {
                            if (!stationMeasurementsMap.containsKey(stationName)) {
                                stationMeasurementsMap.put(stationName, new ArrayList<>());
                            }
                            stationMeasurementsMap.get(stationName).add(new IService.DoubleInstant(dateTime, value));

                            if (!valueFound || value > maxValue) {
                                maxValue = value;
                            }

                            valueFound = true;
                        }
                    }
                } catch (WebApplicationException | ProcessingException exc) {
                    System.out.println(exc.getMessage());
                }
            }

            // Station measurements map ready... Print it now.
            if (!stationMeasurementsMap.isEmpty()) {
                Integer maxNameLength = stationMeasurementsMap.keySet()
                        .stream().map(String::length).max(Integer::compareTo).get();

                int barWidth = 60;
                for (String stationName : nameIdMapSelected.keySet()) {
                    if (stationMeasurementsMap.containsKey(stationName)) {
                        List<IService.DoubleInstant> measurements = stationMeasurementsMap.get(stationName);
                        Collections.sort(measurements);

                        for (IService.DoubleInstant measurement : measurements) {
                            LocalDateTime dateTime = measurement.dateTime;
                            double value = measurement.value;
                            builder.append(String.format("%s  %-" + Math.round(maxNameLength * 1.2) + "s  ",
                                    dateTime, "(" + stationName + ")"));
                            for (int i = 0; i < value / maxValue * barWidth; i++) {
                                builder.append("#");
                            }
                            if (value / maxValue * barWidth >= 1) {
                                builder.append(String.format("   %.2f\n", value));
                            } else {
                                builder.append(String.format("%.2f\n", value));
                            }
                        }

                        builder.append("\n");
                    } else {
                        builder.append(String.format("____-__-__T__:__  (%s) - no measurements found.\n", stationName));
                    }
                }
                System.out.println(builder.toString());
            } else {
                System.out.println("No valid measurements for given stations!");
            }
        } catch (WebApplicationException | ProcessingException exc) {
            System.out.println(exc.getMessage());
        };
    }

    private int getStationId(Map<String, Integer> nameIdMap, String name, Command.DataProvider provider) {
        int id = -1;
        if (provider.equals(Command.DataProvider.GIOS)) {
            id = nameIdMap.getOrDefault(name, -1);
        } else if (provider.equals(Command.DataProvider.Airly)){
            // TODO: should somehow point out which station it returns! (Kraków station ...)
            // Ideally this method could return a list of IDs for all matching names (id -> name map!)
            for (Map.Entry<String, Integer> entry : nameIdMap.entrySet()) {
                // Simple name matching for now...
                if (entry.getKey().matches(".*" + name + ".*")) {
                    id = entry.getValue();
                    break;
                }
            }
        }

        return id;
    }

    // upper bound in micrograms per cubic meter
    private final Map<String, Double> airQualityNorms = new HashMap<String, Double>() {{
        put("C6H6", 5.0);
        put("NO2", 200.0);
        put("SO2", 350.0);
        put("CO", 10000.0);
        put("PM10", 50.0);
        put("PM25", 25.0);
        put("Pb", 0.5);
    }};
}
