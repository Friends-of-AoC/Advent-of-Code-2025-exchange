package com.adventofcode.y25;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Day08 {

    public static final String FILENAME = "input.txt";
    public static final int LIMIT = 1000;
    public static final int NO_LIMIT = -1;

    record Box(long x, long y, long z) {
        public double distanceTo(Box other) {
            return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2) + Math.pow(this.z - other.z, 2));
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Box box = (Box) o;
            return (box.x == this.x && box.y == this.y && box.z == this.z);
        }
    }

    record Distance(Box box1, Box box2) {
        public double length() {
            return box1.distanceTo(box2);
        }

        @Override
        public int hashCode() {
            return Double.hashCode(length());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Distance distance = (Distance) o;
            return (distance.length() == this.length());
        }
    }

    class Circuit {
        private final Set<Box> boxes = new HashSet<>();

        Circuit(Box box) {
            boxes.add(box);
        }

        Circuit(Box box1, Box box2) {
            Collections.addAll(boxes, box1, box2);
        }

        public boolean addBox(Box box) {
            return boxes.add(box);
        }

        public boolean addAllBoxes(Collection<Box> newBoxes) {
            return boxes.addAll(newBoxes);
        }

        public boolean contains(Box box) {
            return boxes.contains(box);
        }

        public long size() {
            return boxes.size();
        }

        public List<Box> getBoxes() {
            return new ArrayList<>(boxes);
        }

        @Override
        public String toString() {
            return boxes.toString();
        }
    }

    public static void main(String[] args) throws IOException {
        Day08 day08 = new Day08();
        day08.execute();
    }

    public void execute() throws IOException {
        List<String> lines = Files.readAllLines(Path.of(FILENAME));
        List<Box> boxes = lines.stream().map(line -> {
            String[] parts = line.split(",");
            return new Box(Long.parseLong(parts[0]), Long.parseLong(parts[1]), Long.parseLong(parts[2]));
        }).toList();

        Long resultPart1 = getCircuits(boxes, LIMIT);
        System.out.println("Part 1: Product of the size of the three largest circuits: " + resultPart1);

        Long resultPart2 = getCircuits(boxes, NO_LIMIT);
        System.out.println("Part 2: Distance from the wall: " + resultPart2);
    }

    public long getCircuits(List<Box> boxes, int limit) {
        List<Circuit> circuits = new ArrayList<>();

        List<Distance> shortestDistances = limit != NO_LIMIT
                ? getAllConnections(boxes).stream().limit(limit).collect(Collectors.toList())
                : getAllConnections(boxes);

        // if unlimited, pre-create circuits for each box
        if (limit == NO_LIMIT) {
            boxes.forEach(box -> circuits.add(new Circuit(box)));
        }

        for (Distance distance : shortestDistances) {
            int index1 = findCircuitIndexContainingBox(circuits, distance.box1);
            int index2 = findCircuitIndexContainingBox(circuits, distance.box2);
            if (index1 == -1 && index2 == -1) {
                // no circuit contains either box, create a new circuit
                circuits.add(new Circuit(distance.box1, distance.box2));
            } else if (index1 == -1) {
                circuits.get(index2).addBox(distance.box1);
            } else if (index2 == -1) {
                circuits.get(index1).addBox(distance.box2);
            } else {
                // both boxes are in different circuits, merge them
                if (index1 != index2) {
                    Circuit circuit1 = circuits.get(index1);
                    Circuit circuit2 = circuits.get(index2);
                    circuit1.addAllBoxes(circuit2.getBoxes());
                    circuits.remove(index2);
                }
            }
            if (limit == NO_LIMIT) {
                if (circuits.size() == 1) {
                    // all boxes are connected in a single circuit
                    return distance.box1.x * distance.box2.x;
                }
            }
        }

        // get three largest circuits
        List<Circuit> threeLargestCircuits = circuits.stream()
                .sorted(Comparator.comparingLong(Circuit::size).reversed())
                .limit(3)
                .toList();

        return threeLargestCircuits.stream().mapToLong(Circuit::size).reduce(1, (a, b) -> a * b);
    }

    private int findCircuitIndexContainingBox(List<Circuit> circuits, Box box) {
        return java.util.stream.IntStream.range(0, circuits.size())
                .filter(i -> circuits.get(i).contains(box))
                .findFirst()
                .orElse(-1);
    }

    public List<Distance> getAllConnections(List<Box> boxes) {
        List<Distance> allConnections = boxes.stream()
                .flatMap(box -> boxes.stream()
                        .filter(otherBox -> box != otherBox)
                        .map(otherBox -> new Distance(box, otherBox))
                ).toList();

        return allConnections.stream()
                .sorted(Comparator.comparingDouble(Distance::length))
                .filter(new HashSet<>()::add).toList();
    }
}