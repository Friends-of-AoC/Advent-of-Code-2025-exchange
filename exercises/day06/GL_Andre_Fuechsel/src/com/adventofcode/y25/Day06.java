package com.adventofcode.y25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Day06 {

    public static void main(String[] args) throws IOException {
        Day06 day06 = new Day06();
        day06.execute();
    }

    public void execute() throws IOException {
        List<String> lines = Files.readAllLines(Path.of("input.txt"));

        long grandTotalPart1 = calculateGrandTotalPart1(lines);
        long grandTotalPart2 = calculateGrandTotalPart2(lines);

        System.out.println("Grand total (solution part 1): " + grandTotalPart1);
        System.out.println("Grand total (solution part 2): " + grandTotalPart2);
    }

    private long calculateGrandTotalPart1(List<String> lines) {
        int cols = lines.getFirst().trim().split("\\s+").length;
        List<Long> resultPerColumn = new ArrayList<>();
        String[] operators = lines.getLast().trim().split("\\s+");

        for (int col = 0; col < cols; col++) {
            char operator = operators[col].charAt(0);
            long result = 0;

            for (int row = 0; row < lines.size() - 1; row++) {
                long value = Long.parseLong(lines.get(row).trim().split("\\s+")[col]);
                result = (result == 0) ? value : (operator == '+' ? result + value : result * value);
            }

            resultPerColumn.add(result);
        }

        return resultPerColumn.stream().mapToLong(Long::longValue).sum();
    }

    private long calculateGrandTotalPart2(List<String> lines) {
        int cols = lines.getFirst().trim().split("\\s+").length;
        List<Long> resultPerColumn = new ArrayList<>();
        String[] operators = lines.getLast().trim().split("\\s+");

        List<Integer> columnsSeparators = getEmptyColumns(lines);
        List<Integer> columnStart = new ArrayList<>();
        columnStart.add(0);
        columnStart.addAll(columnsSeparators.stream().map(i -> i + 1).toList());

        for (int col = 0; col < cols; col++) {
            char operator = operators[col].charAt(0);

            long result = processColumn(lines, col, columnStart.get(col), operator);
            resultPerColumn.add(result);
        }

        return resultPerColumn.stream().mapToLong(Long::longValue).sum();
    }

    // get separator columns (empty columns)
    private List<Integer> getEmptyColumns(List<String> lines) {
        int maxlen = lines.stream().mapToInt(String::length).max().orElse(0);

        List<Integer> emptyCols = new ArrayList<>();
        for (int col = 0; col < maxlen; col++) {
            int column = col; // for lambda scope
            boolean empty = lines.stream()
                    .map(line -> line.length() > column ? line.charAt(column) : ' ')
                    .allMatch(ch -> ch == ' ');
            if (empty) {
                emptyCols.add(col);
            }
        }
        return emptyCols;
    }

    // calculate column value based on start column and operator
    private long processColumn(List<String> lines, int col, int startAtCol, char operator) {
        int nrOfLines = lines.size() - 1;
        int len = findLongestNumberLength(lines, col);
        char[][] matrix = new char[len][nrOfLines];

        for (int c = 0; c < len; c++) {
            int column = startAtCol + c;
            for (int row = 0; row < nrOfLines; row++) {
                char[] lineChars = lines.get(row).toCharArray();
                matrix[c][row] = column < lineChars.length ? lineChars[column] : ' ';
            }
        }

        long result = 0;
        for (int row = 0; row < len; row++) {
            String valueStr = new String(matrix[row]).trim();
            long value = valueStr.isEmpty() ? 0 : Long.parseLong(valueStr);
            result = (result == 0) ? value : (operator == '+' ? result + value : result * value);
        }
        return result;
    }

    private int findLongestNumberLength(List<String> lines, int col) {
        return lines.stream()
                .limit(lines.size() - 1)
                .map(line -> line.trim().split("\\s+")[col])
                .max(Comparator.comparingInt(String::length))
                .orElse("")
                .length();
    }
}