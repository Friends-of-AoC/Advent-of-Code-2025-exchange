package aoc;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;

public class Impl {

    public static void main(String[] args) throws IOException {

        Point2D threeByThree = new Point2D(3,3);
        var reader = new BufferedReader(new InputStreamReader(System.in));
        final PresentShape[] presents;
        {
            var presentArray = new ArrayList<PresentShape>();
            do {
                reader.mark(100);
                var line = reader.readLine();
                if (line.contains("x")) {
                    reader.reset();
                    break;
                }
                var bitset = new BitSet();
                var bitIndex = 0;
                for (int lineIndex = 0; lineIndex < 3; ++lineIndex) {
                    line = reader.readLine();
                    for (int charIndex = 0; charIndex < 3; ++charIndex) {
                        if (line.charAt(charIndex) == '#') {
                            bitset.set(bitIndex);
                        }
                        ++bitIndex;
                    }
                }
                var present = PresentShape.from(new Form(bitset, threeByThree));
                presentArray.add(present);
                System.out.println("Build Present size:" + present.size() + " options:" + present.options().size());
                System.out.println(present.options().get(0));
                reader.readLine(); // empty!
            } while (true);
            presents = presentArray.toArray(new PresentShape[presentArray.size()]);
        }
        AtomicInteger trivialTrue = new AtomicInteger(0);
        AtomicInteger trivialFail = new AtomicInteger(0);
        AtomicInteger nontrivial = new AtomicInteger(0);

        var result = reader
                .lines()
                .filter(l -> l.contains("x"))
                .map(l -> scanRegionLine(l, presents))
                .filter( p -> {
                    if (p.trivialCheckDimensionsLowerBound()) {
                        trivialTrue.incrementAndGet();
                        return false;
                    }
                    return true;
                })
                .filter(p -> {
                    if (!p.trivialCheckDimensionUpperBound()) {
                        trivialFail.incrementAndGet();
                        return false;
                    }
                    return true;
                }).filter(Region::checkDegreesOfFreedom)
                .count();
        System.out.println("TrivialTrue " + trivialTrue + " trivialFail " + trivialFail+ "result " + result);

    }

    static Region scanRegionLine(String line, PresentShape[] presents) {
        var parts = line.split(":? +");
        int[] amounts = new int[parts.length-1];
        for (var idx=0;idx<amounts.length;++idx) {
            amounts[idx] = Integer.parseInt(parts[idx+1]);
        }
        var idx = line.indexOf('x');
        var dimension = new Point2D(Integer.parseInt(parts[0].substring(0, idx)), Integer.parseInt(parts[0].substring(idx+1)));
        return Region.of(dimension, amounts, presents);
    }

}
