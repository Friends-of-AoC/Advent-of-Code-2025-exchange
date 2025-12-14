package aoc;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class Impl {
    public static void main(String[] args) {
        System.out.println(new Impl().scan(System.in));
    }
    long scan(InputStream in) {
        final TreeMap<String, Set<String>> connections = new TreeMap<>();
        final var ids = new ArrayList<String>();
        final AtomicInteger pathes = new AtomicInteger(0);

        class Path  {
            String current;
            BitSet contained;

            Path(String current, BitSet contained) {
                this.current = current;
                this.contained = contained;
            }

            Path buildNew(String target) {
                System.out.println("Build new : " + target + " from " + contained.stream().mapToObj(ids::get).toList());
                if (target.equals("out")) {
                    System.out.println("out");
                    pathes.incrementAndGet();
                    return null;
                }
                int id = ids.indexOf(target);
                if (id == -1) {
                    System.out.println("Invalid target");
                    return null;
                }
                if (contained.get(id)) {
                    System.out.println("loop");
                    return null;
                }
                BitSet next = (BitSet) contained.clone();
                next.set(id);
                var result = new Path(target, next);
                System.out.println("Build " + result);
                return result;
            }
            public String toString() {
                return "{" + current + " -> " + contained.stream().mapToObj(ids::get).toList()+ "}";
            }
            Stream<Path> targets() {
                System.out.println("target for  = " + current +  " : " + connections.get(current));
                return connections.get(current).stream().map(this::buildNew).filter(o -> o != null);
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
        ids.addAll(connections.keySet());
        System.out.println(ids);
        System.out.println(connections);

        BitSet initial = new BitSet();
        var youId = ids.indexOf("you");
        initial.set(youId);
        Path you = new Path("you", initial);
        List<Path> paths = List.of(you);
        while (!paths.isEmpty()) {
            System.out.println("Recurse = " + paths.size());
            paths = paths.stream().flatMap(Path::targets).toList();
        }
        return pathes.get();
    }
}
