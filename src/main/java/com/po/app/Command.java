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
        f1, f2, f3, f4, f5, f6, f7;
    }

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    private boolean parsed;
    private DataProvider provider;
    private boolean caching;
    private FunctionalityChoice functionality;

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

        Options options = new Options();
        OptionGroup optionGroup = new OptionGroup();

        optionGroup.addOption(func1);
        optionGroup.addOption(func2);
        optionGroup.addOption(func3);
        optionGroup.addOption(func4);
        optionGroup.setRequired(true);
        options.addOptionGroup(optionGroup);

        options.addOption(sourceType);
        options.addOption(caching);

        try {
            CommandLine cmd = parser.parse(options, args);
            this.processCommand(cmd);
            this.parsed = true;
        } catch (ParseException exc) {
            String header = "Data provider name must be passed. " +
                            "Also exactly one functionality option (e.g. '-f1') must be passed.";
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(120,"java -jar air_condition.jar",
                    header,options, "", true);
        }
    }

    private void processCommand(CommandLine cmd) throws ParseException {
        this.caching = cmd.hasOption("c");

        String dataProvider = cmd.getOptionValue("d");
        if (DataProvider.names.contains(dataProvider)) {
            this.provider = DataProvider.valueOf(dataProvider);
        }

        if (cmd.hasOption("f1")) {
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
                this.dateTimes.add(LocalDateTime.parse(args[2], this.formatter));
                this.dateTimes.add(LocalDateTime.parse(args[3], this.formatter));
            } catch (DateTimeException exc) {
                throw new ParseException("");
            }

            this.functionality = FunctionalityChoice.f3;

        } else if (cmd.hasOption("f4")) {
            String[] args = cmd.getOptionValues("f4");
            int i = 0;
            for (; i < args.length - 1; i++) {
                this.stationNames.add(args[i]);
            }
            try {
                this.dateTimes.add(LocalDateTime.parse(args[i], this.formatter));
            } catch (DateTimeException exc) {
                throw new ParseException("");
            }

            this.functionality = FunctionalityChoice.f4;

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
}
