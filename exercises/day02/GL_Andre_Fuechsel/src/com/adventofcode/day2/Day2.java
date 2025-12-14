package com.adventofcode.day2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day2 {

    public static void main(String[] args) throws IOException {
        Day2 day2 = new Day2();
        day2.execute();
    }

    public void execute() throws IOException {
        List<String> ranges = Arrays.stream(Files.readAllLines(Path.of("input.txt"))
                .get(0)
                .split(","))
                .toList();

        long invalidIdsPart1= 0;
        long invalidIdsPart2= 0;

        for (String range : ranges) {
            String[] bounds = range.split("-");
            String lower = bounds[0];
            String upper = bounds[1];
            List<Long> results = processRange(lower, upper);
            invalidIdsPart1 += results.get(0);
            invalidIdsPart2 += results.get(1);
        }

        System.out.println("Number of invalid IDs (solution part 1): " + invalidIdsPart1);
        System.out.println("Number of invalid IDs (solution part 2): " + invalidIdsPart2);
    }

    private List<Long> processRange(String lower, String upper) {
        List<Long> invalidIds = new ArrayList<>();
        invalidIds.add(0L);
        invalidIds.add(0L);
        long lowerNum = Long.parseLong(lower);
        long upperNum = Long.parseLong(upper);

        for (long id = lowerNum; id <= upperNum; id++) {
            String idStr = String.valueOf(id);
            if (isInvalidPart1(idStr)) {
                invalidIds.set(0, invalidIds.get(0) + id);
            }
            if (isInvalidPart2(idStr)) {
                invalidIds.set(1, invalidIds.get(1) + id);
            }
        }

        return invalidIds;
    }

    // check if the ID consists of the same digit repeated
    private boolean isInvalidPart1(String id) {
        return id.matches("(\\d+)\\1");
    }

    // check if the ID consists of a sequence of digits repeated
    private boolean isInvalidPart2(String id) {
        return id.matches("(\\d+)(\\1)+");
    }
}
