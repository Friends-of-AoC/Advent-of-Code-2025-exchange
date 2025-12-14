package com.adventofcode.y25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Day3 {

    public static void main(String[] args) throws IOException {
        Day3 day3 = new Day3();
        day3.execute();
    }

    public void execute() throws IOException {
        List<String> banks = Files.readAllLines(Path.of("input.txt"));

        long joltagePart1 = 0;
        long joltagePart2 = 0;

        for (String bank : banks) {
            joltagePart1 += getJoltageOfBankPart(bank, 2);
            joltagePart2 += getJoltageOfBankPart(bank, 12);
        }

        System.out.println("Maximum joltage (solution part 1): " + joltagePart1);
        System.out.println("Maximum joltage (solution part 2): " + joltagePart2);
    }

    private long getJoltageOfBankPart(String bank, int nrOfBatteries) {
        int start = 0;
        int n = bank.length();
        StringBuilder result = new StringBuilder(nrOfBatteries);

        while (nrOfBatteries > 0) {
            int maxPos = start;
            char maxDigit = '0';

            // The last possible position to start
            int end = n - nrOfBatteries;

            for (int i = start; i <= end; i++) {
                char c = bank.charAt(i);
                if (c > maxDigit) {
                    maxDigit = c;
                    maxPos = i;
                    if (maxDigit == '9') break;
                }
            }

            result.append(maxDigit);
            start = maxPos + 1;
            nrOfBatteries--;
        }

        return Long.parseLong(result.toString());
    }
}
