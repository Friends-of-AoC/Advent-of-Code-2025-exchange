package com.adventofcode.y25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day11 {

    record Device(String input, List<String> outputs) {
    }

    record State(String currentDevice, boolean hasDac, boolean hasFft) {
    }

    public static final boolean EXAMPLE_RUN = false;

    public static final String EXAMPLE1 = "example1.txt";
    public static final String EXAMPLE2 = "example2.txt";
    public static final String FILENAME = "input.txt";
    public static final String OUT = "out";
    public static final String START1 = "you";
    public static final String START2 = "svr";
    public static final String DAC = "dac";
    public static final String FFT = "fft";

    public static void main(String[] args) throws IOException {
        Day11 day11 = new Day11();
        day11.execute();
    }

    public void execute() throws IOException {
        long nrOfPaths1 = findAllPathsForward(parse(Files.readAllLines(Path.of(EXAMPLE_RUN ? EXAMPLE1 : FILENAME))), START1, false);
        System.out.println("Nr of paths from " + START1 + " to " + OUT + ": " + nrOfPaths1);

        long nrOfPaths2 = findAllPathsForward(parse(Files.readAllLines(Path.of(EXAMPLE_RUN ? EXAMPLE2 : FILENAME))), START2, true);
        System.out.println("Nr of paths passing '" + DAC + "' and '" + FFT + "' from " + START2 + " to " + OUT + ": " + nrOfPaths2);
    }

    private List<Device> parse(List<String> lines) {
        return lines.stream().map(line -> {
            String[] parts = line.split(": ");
            String input = parts[0];
            List<String> outputs = List.of(parts[1].split(" "));
            return new Device(input, outputs);
        }).toList();
    }

    private long findAllPathsForward(List<Device> devices, String currentDevice, boolean checkForDacAndFft) {
        return findAllPathsForward(devices, currentDevice, checkForDacAndFft, false, false, new HashMap<>());
    }

    private long findAllPathsForward(List<Device> devices, String currentDevice, boolean checkForDacAndFft, boolean hasDac, boolean hasFft, Map<String, Long> cache) {
        if (currentDevice.equals(OUT)) {
            return hasDac && hasFft || !checkForDacAndFft ? 1 : 0;
        }

        State state = new State(currentDevice, hasDac, hasFft);
        if (cache.containsKey(state.toString())) {
            return cache.get(state.toString());
        }

        long totalPaths = 0;
        for (Device device : devices) {
            if (device.input().equals(currentDevice)) {
                for (String output : device.outputs()) {
                    totalPaths += findAllPathsForward(devices, output, checkForDacAndFft, output.equals(DAC) || hasDac, output.equals(FFT) || hasFft, cache);
                }
                cache.put(state.toString(), totalPaths);
            }
        }
        return totalPaths;
    }
}