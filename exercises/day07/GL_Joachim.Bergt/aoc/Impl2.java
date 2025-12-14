package aoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Impl2 {
    public static void main(String[] args) throws IOException {
        System.out.println(countTimeLines(System.in));
    }
    public static long countTimeLines(InputStream in) throws IOException {
        var bin = new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII));
        var line = bin.readLine();
        int start = line.indexOf("S");
        Set<Integer> currentBeams = new TreeSet<>();
        Set<Integer> newBeams = new TreeSet<>();
        Set<Integer> deletedBeams = new TreeSet<>();
        currentBeams.add(start);
        int nr = 1;
        long[] timelines = new long[line.length()];
        Arrays.fill(timelines, 0);
        timelines[start] = 1;
        while ( ( line = bin.readLine()) != null) {
            newBeams.clear();
            deletedBeams.clear();
            for (var beam : currentBeams) {
                var times = timelines[beam];;
                if (line.charAt(beam) == '^') {
                    timelines[beam-1] += times;
                    newBeams.add(beam - 1);
                    timelines[beam+1] += times;
                    newBeams.add(beam + 1);
                    timelines[beam] = 0;
                }
            }
            currentBeams.removeAll(deletedBeams);
            currentBeams.addAll(newBeams);
        }
        return LongStream.of(timelines).sum();
    }
}
