package aoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

public class Impl {
    public static int parser(InputStream in) throws IOException {
        var bin = new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII));
        class Ranges {
            record Range(long from, long to) {
                public boolean contains(long ingredient) {
                    return ingredient >= this.from && ingredient <= this.to;
                }
            }
            Set<Range> ranges = new HashSet<>();
            void add(long from, long to) {
                ranges.add(new Range(from, to));
            }
            public boolean contains(final long ingredient) {
                return ranges.stream().anyMatch( r -> r.contains(ingredient));
            }
        }
        var knownIngredient = new Ranges();
        var freshIngredients = new HashSet<Long>();
        String line;
        while ( ! ( line = bin.readLine()).isEmpty() ) {
            int dash = line.indexOf('-');
            long start = Long.parseLong(line.substring(0, dash));
            long end = Long.parseLong(line.substring(dash+1));
            knownIngredient.add(start, end);
        }

        while ( ( line = bin.readLine()) != null)  {
            long ingredient = Long.parseLong(line);
            if (knownIngredient.contains(ingredient)) {
                freshIngredients.add(ingredient);
            }
        }
        return freshIngredients.size();
    }
}
