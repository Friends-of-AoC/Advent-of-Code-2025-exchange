package com.adventofcode.y25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day09 {

    record Corner(int x, int y) {
        long areaTo(Corner opposite) {
            return Math.abs((long)(opposite.x - x + 1) * (long)(opposite.y - y + 1));
        }
    }

    record Rectangle(Corner corner, Corner opposite) {
        long area() {
            return corner.areaTo(opposite);
        }
    }

    public static void main(String[] args) throws IOException {
        Day09 day09 = new Day09();
        day09.execute();
    }

    public void execute() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("input.txt"));
        List<Corner> corners = lines.stream().map(line -> {
            String[] parts = line.split(",");
            return new Corner(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }).toList();

        Rectangle rectanglePart1 = calculateLargestRectanglePart1(corners);
        Rectangle rectanglePart2 = calculateLargestRectanglePart2(corners);

        System.out.println("Largest rectangle (solution part 1): " + rectanglePart1 + " with area " + rectanglePart1.area());
        System.out.println("Largest rectangle (solution part 2): " + rectanglePart2 + " with area " + rectanglePart2.area());
    }

    private Rectangle calculateLargestRectanglePart1(List<Corner> corners) {
        return corners.stream()
                .flatMap(corner -> corners.stream().map(opposite -> new Rectangle(corner, opposite)))
                .max((r1, r2) -> Long.compare(r1.area(), r2.area()))
                .orElse(new Rectangle(new Corner(0, 0), new Corner(0, 0)));
    }

    private Rectangle calculateLargestRectanglePart2(List<Corner> corners) {
        // TODO

        System.out.println("""
        =========================================
        TBH, I have no clue how to solve part 2.
        And no time yet...
        =========================================
        """);

        return new Rectangle(new Corner(0,0), new Corner(0,0));
    }

}