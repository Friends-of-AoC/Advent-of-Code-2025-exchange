# Day 8: Playground

Junction boxes in 3D space need to be connected with string lights. Connect pairs closest together to form circuits. Uses Union-Find (Disjoint Set Union) data structure.

## Part 1

**Task:** Connect the 1000 closest pairs of junction boxes, then multiply the sizes of the 3 largest circuits.

Algorithm:
1. Parse X,Y,Z coordinates for each junction box
2. Calculate all pairwise Euclidean distances
3. Sort pairs by distance (ascending)
4. Process 1000 shortest pairs using Union-Find
5. Find circuit sizes and multiply top 3

```groovy
// Union-Find with path compression and union by rank
int find(int x) {
    if (parent[x] != x) parent[x] = find(parent[x])
    return parent[x]
}
```

Example (20 boxes, 10 connections): circuits = [5, 4, 2, 2, 1, 1, ...] → **5 × 4 × 2 = 40**

## Part 2

**Task:** Connect all junction boxes into one circuit. Multiply the X coordinates of the last two boxes connected.

Same Union-Find approach, but continue until `numComponents == 1`:
```groovy
for (pair in sortedPairs) {
    if (uf.union(pair.i, pair.j)) {
        lastPair = pair
        if (uf.allConnected()) break
    }
}
return boxes[lastPair.i].x * boxes[lastPair.j].x
```

Example (20 boxes): last connection (216,146,977) ↔ (117,168,530) → **216 × 117 = 25272**

## Usage

```bash
groovy solution.groovy input.txt        # Part 1
groovy solution_part2.groovy input.txt  # Part 2
```

Or run with test data:
```bash
groovy solution.groovy test.txt
```

Requires Groovy (`sudo apt install groovy` on Ubuntu/Debian, or via SDKMAN).
