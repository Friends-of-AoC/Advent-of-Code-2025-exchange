package com.adventofcode.y25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day07 {

    record Result(long nrOfSplits, long nrOfTimelines) {}

    private char START = 'S';
    private char SPLITTER = '^';

    public static void main(String[] args) throws IOException {
        Day07 day07 = new Day07();
        day07.execute();
    }

    public void execute() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("input.txt"));

        Result result = split(lines);

        System.out.println("Nr of tachyon splits (solution part 1): " + result.nrOfSplits);
        System.out.println("Nr of timelines (solution part 2): " + result.nrOfTimelines);
    }

    private Result split(List<String> lines) {
        int nrOfSplits = 0;
        List<Integer> beamIndexes = new ArrayList<>();
        List<Integer> nextBeamIndexes = new ArrayList<>();
        long[] timelines = new long[lines.size()];

        // start
        beamIndexes.add(lines.get(0).indexOf(START));
        timelines[lines.get(0).indexOf(START)] = 1;

        // start
        beamIndexes.add(lines.get(0).indexOf(START));

        // iterate downwards
        for (int row = 0; row < lines.size() - 1; row++) {
            String line = lines.get(row);
            for (int beamIndex : beamIndexes) {
                // check for split on next line
                if (line.charAt(beamIndex) == SPLITTER) {
                    // split found, add two new beams
                    nrOfSplits++;

                    // time splits too
                    timelines[beamIndex - 1] += timelines[beamIndex];
                    timelines[beamIndex + 1] += timelines[beamIndex];

                    // don't forget to cancel this timeline
                    timelines[beamIndex] = 0;

                    // add new beams
                    nextBeamIndexes.add(beamIndex - 1);
                    nextBeamIndexes.add(beamIndex + 1);
                } else {
                    // no split, continue with same beam
                    nextBeamIndexes.add(beamIndex);
                }
            }
            beamIndexes = nextBeamIndexes.stream().distinct().toList();
            nextBeamIndexes.clear();
        }

        return new Result(nrOfSplits, Arrays.stream(timelines).sum());
    }
}