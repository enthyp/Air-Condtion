package com.po.app;

import org.apache.commons.cli.*;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.cli.Option.UNLIMITED_VALUES;

public class Command {

    public enum DataProvider {
        GIOS,
        Airly;

        public static Set<String> names = new HashSet<>(Arrays
                .stream(DataProvider.values())
                .map(DataProvider::name)
                .collect(Collectors.toSet()));
    }

    public enum FunctionalityChoice {
        s, f1, f2, f3, f4, f5, f6, f7, f8
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    private boolean parsed;
    private DataProvider provider;
    private boolean caching;
    private FunctionalityChoice functionality;

    private int N;
    private List<String> stationNames = new ArrayList<>();
    private List<String> parameterNames = new ArrayList<>();
    private List<LocalDateTime> dateTimes = new ArrayList<>();

    public Command(String[] args) {
        this.parsed = false;
        this.parse(args);
    }

    private void parse(String[] args) {
        DefaultParser parser = new DefaultParser();

        Option sourceType = Option.builder("d")
                .required()
                .hasArg()
                .argName("data provider name")
                .desc("either 'GIOS' or 'Airly'")
                .build();

        Option caching = Option.builder("c")
                .desc("if passed air quality data is cached locally")
                .build();

        Option show = Option.builder("s")
                .desc("if passed all available station names are shown")
                .build();

        Option func1 = Option.builder("f1")
                .hasArg()
                .argName("station_name")
                .desc("displays current air quality index value for measuring station")
                .build();

        Option func2 = Option.builder("f2")
                .hasArgs()
                .argName("station name> <parameter name")
                .numberOfArgs(2)
                .desc("displays current value of parameter for measuring station")
                .build();

        Option func3 = Option.builder("f3")
                .hasArgs()
                .argName("station name> <parameter name> <start date> <end date")
                .numberOfArgs(4)
                .desc("displays average value of parameter for given period and measuring station; " +
                        "\ndate format: yyyy-MM-dd_HH:mm:ss")
                .build();

        Option func4 = Option.builder("f4")
                .hasArgs()
                .argName("station name> [<station name> ...] <start date")
                .numberOfArgs(UNLIMITED_VALUES)
                .desc("for each measuring station displays name of parameter that varied the most since start date; " +
                        "\ndate format: yyyy-MM-dd_HH:mm:ss")
                .build();

        Option func5 = Option.builder("f5")
                .hasArgs()
                .argName("station name> <date")
                .numberOfArgs(2)
                .desc("finds parameter that had the lowest value at given datetime and station")
                .build();

        Option func6 = Option.builder("f6")
                .hasArgs()
                .argName("station name> <date> <N")
                .numberOfArgs(3)
                .desc("displays N values of parameters which exceeded air quality norms the most " +
                        "on given date and for given station (sorted in ascending order)")
                .build();

        Option func7 = Option.builder("f7")
                .hasArgs()
                .argName("station name> [<station name> ...] <parameter name")
                .numberOfArgs(UNLIMITED_VALUES)
                .desc("displays date and station for which given parameter achieved the maximum and " +
                        "minimum value (of all given stations)")
                .build();

        Option func8 = Option.builder("f8")
                .hasArgs()
                .argName("parameter name> <station name> [<station name> ...] <start date> <end date")
                .numberOfArgs(UNLIMITED_VALUES)
                .desc("displays ASCII plots of parameter values for all given stations in given time span")
                .build();

        Options options = new Options();
        OptionGroup optionGroup = new OptionGroup();

        optionGroup.addOption(show);
        optionGroup.addOption(func1);
        optionGroup.addOption(func2);
        optionGroup.addOption(func3);
        optionGroup.addOption(func4);
        optionGroup.addOption(func5);
        optionGroup.addOption(func6);
        optionGroup.addOption(func7);
        optionGroup.addOption(func8);
        optionGroup.setRequired(true);
        options.addOptionGroup(optionGroup);

        options.addOption(sourceType);
        options.addOption(caching);

        try {
            CommandLine cmd = parser.parse(options, args);
            this.processCommand(cmd);
            this.parsed = true;
        } catch (InputParseException exc) {
            System.out.println("Problem occurred: " + exc.getMessage());
            String header = "Data provider name must be passed. " +
                            "Also exactly one functionality option (e.g. '-f1') must be passed.";
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(120,"java -jar air_condition.jar",
                    header,options, "", true);
        } catch (ParseException exc) {
            String header = "Data provider name must be passed. " +
                    "Also exactly one functionality option (e.g. '-f1') must be passed.";
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(120,"java -jar air_condition.jar",
                    header,options, "", true);
        }
    }

    private void processCommand(CommandLine cmd) throws InputParseException {
        this.caching = cmd.hasOption("c");

        String dataProvider = cmd.getOptionValue("d");
        if (DataProvider.names.contains(dataProvider)) {
            this.provider = DataProvider.valueOf(dataProvider);
        } else {
            throw new InputParseException("No such provider! Use Airly or GIOS instead!");
        }

        if (cmd.hasOption("s")) {
            this.functionality = FunctionalityChoice.s;
        } else if(cmd.hasOption("f1")) {
            this.stationNames.add(cmd.getOptionValue("f1"));
            this.functionality = FunctionalityChoice.f1;
        } else if (cmd.hasOption("f2")) {
            String[] args = cmd.getOptionValues("f2");
            this.stationNames.add(args[0]);
            this.parameterNames.add(args[1]);
            this.functionality = FunctionalityChoice.f2;
        } else if (cmd.hasOption("f3")) {
            String[] args = cmd.getOptionValues("f3");
            this.stationNames.add(args[0]);
            this.parameterNames.add(args[1]);
            try {
                LocalDateTime dateFrom = LocalDateTime.parse(args[2], this.formatter);
                LocalDateTime dateTo = LocalDateTime.parse(args[3], this.formatter);
                if (dateFrom.isBefore(dateTo) || dateFrom.isEqual(dateTo)) {
                    this.dateTimes.add(dateFrom);
                    this.dateTimes.add(dateTo);
                } else {
                    throw new InputParseException("Start date should precede end date!");
                }
            } catch (DateTimeException exc) {
                throw new InputParseException("Incorrect date format!");
            }

            this.functionality = FunctionalityChoice.f3;

        } else if (cmd.hasOption("f4")) {
            // TODO: add data structure that encapsulates adding typical arguments like station names, dates etc.
            String[] args = cmd.getOptionValues("f4");
            if (args.length >= 2) {
                int i = 0;
                for (; i < args.length - 1; i++) {
                    this.stationNames.add(args[i]);
                }
                try {
                    this.dateTimes.add(LocalDateTime.parse(args[i], this.formatter));
                } catch (DateTimeException exc) {
                    throw new InputParseException("Incorrect date format!");
                }

                this.functionality = FunctionalityChoice.f4;
            } else {
                throw new InputParseException("Too few arguments!");
            }
        } else if (cmd.hasOption("f5")) {
            String[] args = cmd.getOptionValues("f5");
            this.stationNames.add(args[0]);
            try {
                this.dateTimes.add(LocalDateTime.parse(args[1], this.formatter));
            } catch (DateTimeException exc) {
                throw new InputParseException("Incorrect date format!");
            }

            this.functionality = FunctionalityChoice.f5;
        } else if (cmd.hasOption("f6")) {
            String[] args = cmd.getOptionValues("f6");
            this.stationNames.add(args[0]);
            try {
                this.dateTimes.add(LocalDateTime.parse(args[1], this.formatter));
            } catch (DateTimeException exc) {
                throw new InputParseException("Incorrect date format!");
            }
            try {
                this.N = Integer.valueOf(args[2]);
                if (N <= 0) {
                    throw new InputParseException("N should be > 0");
                }
            } catch (NumberFormatException exc) {
                throw new InputParseException("N must be an integer!");
            }

            this.functionality = FunctionalityChoice.f6;
        } else if (cmd.hasOption("f7")) {
            String[] args = cmd.getOptionValues("f7");
            if (args.length >= 2) {
                int i = 0;
                for (; i < args.length - 1; i++) {
                    this.stationNames.add(args[i]);
                }
                this.parameterNames.add(args[i]);

                this.functionality = FunctionalityChoice.f7;
            } else {
                throw new InputParseException("Too few arguments!");
            }
        }  else if (cmd.hasOption("f8")) {
            String[] args = cmd.getOptionValues("f8");
            if (args.length >= 4) {
                this.parameterNames.add(args[0]);
                int i = 1;
                for (; i < args.length - 2; i++) {
                    this.stationNames.add(args[i]);
                }
                try {
                    LocalDateTime dateFrom = LocalDateTime.parse(args[i], this.formatter);
                    LocalDateTime dateTo = LocalDateTime.parse(args[i+1], this.formatter);
                    if (dateFrom.isBefore(dateTo) || dateFrom.isEqual(dateTo)) {
                        this.dateTimes.add(dateFrom);
                        this.dateTimes.add(dateTo);
                    } else {
                        throw new InputParseException("Start date should precede end date!");
                    }
                } catch (DateTimeException exc) {
                    throw new InputParseException("Incorrect date format!");
                }

                this.functionality = FunctionalityChoice.f8;
            } else {
                throw new InputParseException("Too few arguments!");
            }
        }
    }

    public boolean parsed() {
        return parsed;
    }

    public DataProvider getProvider() {
        return provider;
    }

    public boolean useCaching() {
        return caching;
    }

    public FunctionalityChoice getFunctionality() {
        return functionality;
    }

    public List<String> getStationNames() {
        return stationNames;
    }

    public List<String> getParameterNames() {
        return parameterNames;
    }

    public List<LocalDateTime> getDateTimes() {
        return dateTimes;
    }

    public int getN() { return N; }

    private class InputParseException extends Exception {
        private String message;

        public InputParseException(String message) {
            super(message);
            this.message = message;
        }

        @Override
        public String toString() {
            return message;
        }
    }
}
