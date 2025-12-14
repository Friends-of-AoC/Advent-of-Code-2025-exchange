package com.adventofcode.y25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day05 {

    public static void main(String[] args) throws IOException {
        Day05 day05 = new Day05();
        day05.execute();
    }

    public void execute() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("input.txt"));

        List<String> ranges = new ArrayList<>();
        List<Long> ingredients = new ArrayList<>();

        // create lists of ranges and ingredients from input lines
        ranges.addAll(lines.subList(0, lines.indexOf("")));
        ingredients.addAll(lines.subList(lines.indexOf("") + 1, lines.size()).stream()
                .map(Long::valueOf)
                .toList());

        // merge overlapping ranges
        var mergedRanges = merge(ranges);

        long nrOfFreshIngredientsPart1 = ingredients.stream()
                .filter(ingredient -> isFresh(ingredient, mergedRanges))
                .count();

        long nrOfFreshIngredientsPart2 = mergedRanges.stream()
                .mapToLong(range -> {
                    String[] bounds = range.split("-");
                    long lowerBound = Long.valueOf(bounds[0]);
                    long upperBound = Long.valueOf(bounds[1]);
                    return upperBound - lowerBound + 1;
                })
                .sum();

        System.out.println("Number of fresh ingredients (solution part 1): " + nrOfFreshIngredientsPart1);
        System.out.println("Number of fresh ingredients (solution part 2): " + nrOfFreshIngredientsPart2);
    }

    private List<String> merge(List<String> ranges) {
        ranges.sort(Comparator.comparingLong(r -> Long.parseLong(r.split("-")[0])));

        List<String> mergedRanges = new ArrayList<>();
        String currentRange = ranges.get(0);

        for (int i = 1; i < ranges.size(); i++) {
            String[] currentBounds = currentRange.split("-");
            String[] nextBounds = ranges.get(i).split("-");

            long currentLower = Long.parseLong(currentBounds[0]);
            long currentUpper = Long.parseLong(currentBounds[1]);
            long nextLower = Long.parseLong(nextBounds[0]);
            long nextUpper = Long.parseLong(nextBounds[1]);

            if (nextLower <= currentUpper + 1) {
                currentRange = currentLower + "-" + Math.max(currentUpper, nextUpper);
            } else {
                mergedRanges.add(currentRange);
                currentRange = ranges.get(i);
            }
        }
        mergedRanges.add(currentRange);

        return mergedRanges;
    }

    private boolean isFresh(Long ingredient, List<String> ranges) {
        for (String range : ranges) {
            String[] bounds = range.split("-");
            long lowerBound = Long.valueOf(bounds[0]);
            long upperBound = Long.valueOf(bounds[1]);

            if (ingredient >= lowerBound && ingredient <= upperBound) {
                return true;
            }
        }
        return false;
    }
}