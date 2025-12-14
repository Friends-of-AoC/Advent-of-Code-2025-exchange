package aoc;

import java.io.BufferedReader;import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;

public class Impl {
    public static void main(String[] args) throws IOException {
        System.out.println(countSplits(System.in));
    }
    public static int countSplits(InputStream in) throws IOException {
        var bin = new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII));
        var line = bin.readLine();
        int start = line.indexOf("S");
        int splits = 0;
        Set<Integer> currentBeams = new TreeSet<>();
        Set<Integer> removeBeams = new TreeSet<>();
        Set<Integer> addBeans = new TreeSet<>();
        currentBeams.add(start);
        while ( ( line = bin.readLine()) != null) {
            removeBeams.clear();;
            addBeans.clear();
            for (var beam : currentBeams) {
                if (line.charAt(beam) == '^') {
                    ++splits;
                    removeBeams.add(beam);
                    addBeans.add(beam-1);
                    addBeans.add(beam+1);
                }
            }
            currentBeams.removeAll(removeBeams);
            currentBeams.addAll(addBeans);
        }
        return splits;
    }
}
