package aoc;

import java.io.InputStream;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Impl {
    public static void main(String[] args) {
        System.out.println(Impl.solve(System.in));
    }
    static long solve(InputStream in) {
        record Box(long x, long y, long z) {
            double distance(Box other) {
                return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y,2) + Math.pow(this.z - other.z,2));
            }

            @Override
            public String toString() {
                return "{" + x +","+y+"," +z + "}";
            }
        };

        record DirectConnection(Set<Box> boxes, double length) {
            static DirectConnection buildSimple(Box a, Box b) {
                var s = new HashSet<Box>();
                s.add(a);
                s.add(b);
                return new DirectConnection(s, a.distance(b));
            }

            public int compareDistances(DirectConnection o) {
                return Double.compare(this.length, o.length);
            }
        }
        var scanner = new Scanner(in);
        scanner.useLocale(Locale.US);
        scanner.useDelimiter(Pattern.compile("[ \\t\\n\\x0B\\f\\r,]+"));
        Set<Box>  unconnectedBoxes
                = new HashSet<>();
        var pairs = new ArrayList<DirectConnection>();
        while (scanner.hasNextLong()) {
            var rec = new Box(scanner.nextLong(), scanner.nextLong(), scanner.nextLong());
            for (Box point : unconnectedBoxes) {
                pairs.add(DirectConnection.buildSimple(rec, point));
            }
            unconnectedBoxes.add(rec);
//            System.out.println("scanner.nextLong() = " + rec);
        }
//        System.out.println("pairs.size() = " + pairs.size());
        record Circuit(Set<Box> boxes) {

            public Optional<Circuit> merge(DirectConnection other) {
                return merge(other.boxes);
            }
            public Optional<Circuit> merge(Circuit other) {
                return merge(other.boxes);
            }
            private Optional<Circuit> merge(Set<Box> other) {
                var copy = new HashSet<Box>();
                copy.addAll(this.boxes);
                copy.addAll(other);
                if (copy.size() < (this.boxes.size() + other.size())) {
                    return Optional.of(new Circuit(copy));
                }
                return Optional.empty();
            }
        }
        var circuits = new HashSet<Circuit>();
        pairs.sort(DirectConnection::compareDistances);
        /// DEBUG
        for (var p : pairs) {
//            System.out.println(p);
        }
        Supplier<Integer> connectionCounter = new Supplier<Integer>() {
            @Override
            public Integer get() {
                return circuits.stream().mapToInt(circuit -> circuit.boxes.size()-1).sum();
            }
        };
        Supplier<Integer> circuitCounter = new Supplier<Integer>() {
            @Override
            public Integer get() {
                return circuits.size();
            }
        };
        Supplier<Long> resulter = new Supplier<Long>() {
            @Override
            public Long get() {
                var array = circuits.stream().map(circuit -> circuit.boxes.size()).collect(Collectors.toList());
                array.sort(Comparator.reverseOrder());
                long result = 1;
                for (int i=0;i<3&&i<array.size();++i) {
                    result *= array.get(i);
                }
                return result;
            }
        };
        var pairIterator = pairs.iterator();
        var shortestConnectionCounter = 0;
        while (shortestConnectionCounter <1000 && pairIterator.hasNext()) {


            var currentPair = pairIterator.next();
//            System.out.println("check : " + currentPair.boxes);
            Optional<Circuit> next = Optional.empty();
            var  removed = new ArrayList<Circuit>();
            boolean fullyContained = false;
            for (var known : circuits) {
                if (known.boxes.containsAll(currentPair.boxes)) {
                    fullyContained = true;
                }
                if (next.isEmpty()) {
                    next = known.merge(currentPair);
                    if (next.isPresent()) {
//                        System.out.println("merge into existing circuit = " + known.boxes);
                        removed.add(known);
                    }
                } else {
                    var nextNext = next.get().merge(known);
                    if (nextNext.isPresent()) {
//                        System.out.println("continue merge = " + known.boxes);
                        removed.add(known);
                        next = nextNext;
                    }
                }
            }
            if (! removed.isEmpty()) {
                ++shortestConnectionCounter;
                circuits.removeAll(removed);
                circuits.add(next.get());
//                System.out.println("consolidated");
            } else if(! fullyContained) {
                ++shortestConnectionCounter;
//                System.out.println("new Circuit");
                circuits.add(new Circuit(currentPair.boxes));
            }
            unconnectedBoxes.removeAll(currentPair.boxes);

//            List<Integer> sizes = circuits.stream().map(c -> c.boxes.size()).collect(Collectors.toList());
//            System.out.println("Connections:" + shortestConnectionCounter + " connectedCircuits:" + sizes + " Mult(3):"+resulter.get()+" Unconnected Boxes:" + unconnectedBoxes.size());
        }
        return resulter.get();
    }
}
