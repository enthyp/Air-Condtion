package com.po.app;

import com.po.app.data.IService;
import com.po.app.data.ServiceFactory;

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

            switch (cmd.getFunctionality()) {
                case f1: functionality1();
                         break;
                case f2: functionality2();
                    break;
                case f3: functionality3();
                    break;
                case f4: functionality4();
                    break;
            }
        }
    }

    private void functionality1() {
        String stationName = cmd.getStationNames().get(0);
        //TODO: handle server failure, timeouts on calls without parameters (in other cases they should work fine)
        Map<String, Integer> nameIdMap = this.service.getNameIdMap();

        int id = this.getStationId(nameIdMap, stationName, this.provider);
        try {
            IService.StringInstant index = this.service.getCurrentIndex(id);

            if (index != null && index.dateTime != null && index.content != null) {
                System.out.println(String.format("Index value for %s: %s", index.dateTime, index.content));
            } else {
                if (id == -1)
                    System.out.println("Station not found!");
                else
                    System.out.println("Empty response!");
            }
        } catch (WebApplicationException exc) {
            System.out.println(exc.getMessage());
        }
    }

    private void functionality2() {
        String stationName = cmd.getStationNames().get(0);
        String parameterName = cmd.getParameterNames().get(0);

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
        } catch (WebApplicationException exc) {
            System.out.println(exc.getMessage());
        }
    }

    private void functionality3() {
        String stationName = cmd.getStationNames().get(0);
        String parameterName = cmd.getParameterNames().get(0);
        LocalDateTime dateFrom = cmd.getDateTimes().get(0);
        LocalDateTime dateTo = cmd.getDateTimes().get(1);

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
    }

    private void functionality4() {
        List<String> stationNames = cmd.getStationNames();
        LocalDateTime dateFrom = cmd.getDateTimes().get(0);

        // Get IDs of given stations.
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
            Map<IService.StringInstant, Double> paramValues =
                    this.service.getParamValue(entry.getValue(), new ArrayList<>(), new ArrayList<>());

            // First add all parameter measurements to parameter name -> List<(time instant, value)> map.
            Map<String, List<IService.DoubleInstant>> paramValuesSorted = new HashMap<>();
            for (IService.StringInstant stringInstant : paramValues.keySet()) {
                String parameterName = stringInstant.content;
                LocalDateTime dateTime = stringInstant.dateTime;
                double value = paramValues.get(stringInstant);

                if (parameterName != null && dateTime != null  &&
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
        }
    }

    private int getStationId(Map<String, Integer> nameIdMap, String name, Command.DataProvider provider) {
        int id = -1;
        if (provider.equals(Command.DataProvider.GIOS)) {
            id = nameIdMap.getOrDefault(name, -1);
        } else if (provider.equals(Command.DataProvider.Airly)){
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
}
