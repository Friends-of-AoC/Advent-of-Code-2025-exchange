package aoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.TreeSet;

public class Impl2 {
    public static long parser(InputStream in) throws IOException {
        var bin = new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII));
        class Ranges {
            record Range(long from, long to) implements Comparable{
                long length() {
                    return this.to - this.from + 1;
                }
                @Override
                public int compareTo(Object o) {
                    if (o instanceof Range or) {
                        if (this.from < or.from) return -1;
                        if (this.to > or.to) return 1;
                    }
                    return 0;
                }
            }
            Set<Range> ranges = new TreeSet<>();
            void add(long from, long to) {
                ranges.add(new Range(from, to));
            }
            long collapseRanges() {
                Range current = null;
                long count = 0;
                for (Range range : ranges) {
                    if (current == null) {
                        current = range;
                    }
                    if (range.from > current.to) {
                        count  += current.length();
                        current = range;
                    }
                    if (range.to > current.to) {
                        current = new Range(current.from, range.to);
                    }
                }
                if (current != null) {
                    count += current.length();
                }
                return count;
            }

        }
        var knownIngredient = new Ranges();
        String line;
        while ( ! ( line = bin.readLine()).isEmpty() ) {
            int dash = line.indexOf('-');
            long start = Long.parseLong(line.substring(0, dash));
            long end = Long.parseLong(line.substring(dash+1));
            knownIngredient.add(start, end);
        }
        return knownIngredient.collapseRanges();
    }
}
