package aoc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.*;
import java.util.stream.Stream;

public class Impl2 {
    public static void main(String[] args) {
        System.out.println(new Impl2().scan(System.in));
    }
    long scan(InputStream in) {
        final TreeMap<String, TreeSet<String>> connections = new TreeMap<>();

        record Pair(String start, String currentChild) {
            public void next(TreeMap<String, TreeSet<String>> connections, Consumer<Pair> push, Predicate<Pair> endChecker) {
                if (endChecker.test(this)) {
                    return;
                }
                var children = connections.get(start);
                if (children==null||children.isEmpty()) return;
                String nextChild = this.currentChild == null ? children.first() : children.higher(this.currentChild);
                if (nextChild != null) {
                    push.accept(new Pair(start, nextChild));
                    push.accept(new Pair(nextChild, null));
                }
            }
            Stream<String> stream() {
                if (this.currentChild != null) {
                    return Stream.of(start, this.currentChild);
                } return Stream.of(start);
            }
        }

        new BufferedReader(new InputStreamReader(in, StandardCharsets.US_ASCII)).lines().forEach(line -> {
            int idx = line.indexOf(':');
            String from = line.substring(0, idx);
            var set = connections.computeIfAbsent(from, (key) -> new TreeSet<>());
            idx += 2;
            do {
                var nextIdx = line.indexOf(' ', idx);
                boolean end = false;
                if (nextIdx == -1) {
                    nextIdx = line.length();
                    end = true;
                }
                String to = line.substring(idx, nextIdx);
                set.add(to);
                if (end) {
                    idx = -1;
                } else {
                    idx = nextIdx+1;
                }
            } while (idx != -1);
        });
        class Partial {
            final String from;
            final String to;
            final Set<String> excluding;
            final Set<String> required;
            final AtomicBoolean shouldBeRunning;
            long count = 0;

            Partial(String from, String to, Set<String> excluding, Set<String> required, AtomicBoolean shouldBeRunning) {
                this.from = from;
                this.to = to;
                this.excluding = excluding == null ? Collections.emptySet() : excluding;
                this.required = required == null ? Collections.emptySet() : required;
                this.shouldBeRunning = shouldBeRunning;
            }
            public String toString() {
                return "{Partial from:" + from + ", to:" + to + "}";
            }
        }

        final var breakSet = new AtomicBoolean[] {new AtomicBoolean(true), new AtomicBoolean(true)};

        var partials = new Partial[]{
                new Partial("svr", "fft", Set.of("dac", "out"), null, breakSet[0]),
                new Partial("fft", "dac", Set.of("svr", "out"), null, breakSet[0]),
                new Partial("dac", "out", Set.of("svr", "fft"), null, breakSet[0]),
                new Partial("svr", "dac", Set.of("fft", "out"), null, breakSet[1]),
                new Partial("dac", "fft", Set.of("svr", "out"), null, breakSet[1]),
                new Partial("fft", "out", Set.of("svr", "dac"), null, breakSet[1]),
        };
        Arrays.stream(partials).parallel().forEach(part -> {
            final var queue = new Stack<Pair>();
            queue.push(new Pair(part.from, null));
            final var intermediate = new TreeMap<String, Integer>();
            BiConsumer<Pair, Integer> incrementer = (e, delta) -> {
//                System.out.println(">inc " + part + " :  " + e +"+" + delta + " " + debugQueue.get());
                intermediate.compute(e.start, (key, val) -> val == null ? delta : val + delta);
                if (delta != 0) {
                    queue.forEach(p -> intermediate.compute(p.start,(key, val) -> val == null ? delta : val + delta));
                }
            };
            Consumer<Pair> pusher = (newPair) -> {
                // loop breaker
                if (queue.stream().anyMatch(p -> p.start.equals(newPair.start) || (newPair.currentChild != null && newPair.currentChild.equals(p.start)))) {
                    return;
                }
                if (part.excluding.isEmpty()) {
                    if (newPair.stream().anyMatch(part.excluding::contains)) {
                        return;
                    }
                }
                if (newPair.currentChild == null && intermediate.containsKey(newPair.start)) {
                    var val = intermediate.get(newPair.start);
                    if (val == 0) {
                        return;
                    }
                }
                queue.add(newPair);
            };
            while (! queue.isEmpty() && part.shouldBeRunning.get()) {
                var current = queue.pop();
                if (current != null) {
//                    System.out.println("popped : " + current);
                    current.next(connections, pusher, (p)-> {
                        if (p.start.equals(part.to)) {
                            incrementer.accept(p, 1);
                            return true;
                        }
                        return false;
                    });
                    if (current.currentChild != null) {
                        // depth First: currentChild is done!
                        intermediate.putIfAbsent(current.currentChild, 0);
                    }
                }
            }
            part.count = intermediate.getOrDefault(part.from, 0);
            if (part.count == 0) {
                System.out.println("break computing" + part);
                part.shouldBeRunning.set(false);
            }
            System.out.println("done " + part.from + " .. " + part.to + " in " + part.count);
        });
        long result = 0;
        for (int resultIndex=0;resultIndex<2;++resultIndex) {
            if (breakSet[resultIndex].get()) {
                result += ( partials[resultIndex*3].count * partials[resultIndex*3+1].count * partials[resultIndex*3+2].count);
            }
        }
        return result;
    }
}
