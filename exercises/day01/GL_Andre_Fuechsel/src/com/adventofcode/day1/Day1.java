package com.adventofcode.day1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day1 {

    public static void main(String[] args) throws IOException {
        List<String> lines = Files.readAllLines(Path.of("input.txt"));

        int countClicks = 0;
        int current = 50;

        for (String line : lines) {
            int number = Integer.parseInt(line.substring(1));
            int previous = current;
            boolean isRight = line.startsWith("R");

            // get next number on the dial
            current = isRight
                    ? (current + number) % 100
                    : ((current - number) % 100 + 100) % 100;

            // when turning backwards, check if we passed 0 or reached it
            // starting from 0 doesn't count
            if (!isRight && previous != 0 && (current >= previous || current == 0)) {
                countClicks++;
            }

            // add the number of clicks per full turn
            countClicks += isRight
                    ? (previous + number) / 100
                    : number / 100;
        }

        System.out.println("Number of clicks: " + countClicks);
    }
}
