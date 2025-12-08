// Advent of Code 2025 - Day 8: Playground (Part 1)
// Connect the 1000 closest pairs of junction boxes using Union-Find

class UnionFind {
    int[] parent
    int[] rank
    int[] size

    UnionFind(int n) {
        parent = new int[n]
        rank = new int[n]
        size = new int[n]
        for (int i = 0; i < n; i++) {
            parent[i] = i
            rank[i] = 0
            size[i] = 1
        }
    }

    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x])  // path compression
        }
        return parent[x]
    }

    boolean union(int x, int y) {
        int rootX = find(x)
        int rootY = find(y)
        if (rootX == rootY) return false  // already in same circuit

        // Union by rank
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY
            size[rootY] += size[rootX]
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX
            size[rootX] += size[rootY]
        } else {
            parent[rootY] = rootX
            size[rootX] += size[rootY]
            rank[rootX]++
        }
        return true
    }

    int getSize(int x) {
        return size[find(x)]
    }
}

def solve(String filename) {
    def lines = new File(filename).readLines().findAll { it.trim() }
    
    // Parse junction boxes as [x, y, z] coordinates
    def boxes = lines.collect { line ->
        def parts = line.split(',').collect { it.trim().toLong() }
        [x: parts[0], y: parts[1], z: parts[2]]
    }
    
    int n = boxes.size()
    println "Loaded ${n} junction boxes"

    // Calculate all pairwise distances
    def pairs = []
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            def dx = boxes[i].x - boxes[j].x
            def dy = boxes[i].y - boxes[j].y
            def dz = boxes[i].z - boxes[j].z
            def distSq = dx*dx + dy*dy + dz*dz  // squared distance (no sqrt needed for sorting)
            pairs << [i: i, j: j, distSq: distSq]
        }
    }

    // Sort by distance (ascending)
    pairs.sort { it.distSq }
    println "Calculated ${pairs.size()} pairs"

    // Connect 1000 closest pairs using Union-Find
    def uf = new UnionFind(n)
    int connectionsToMake = Math.min(1000, pairs.size())
    
    for (int k = 0; k < connectionsToMake; k++) {
        def pair = pairs[k]
        uf.union(pair.i, pair.j)  // may or may not create new connection
    }

    // Find all unique circuit sizes
    def circuitSizes = (0..<n).collect { uf.find(it) }
        .unique()
        .collect { root -> uf.getSize(root) }
        .sort { -it }  // descending

    println "Circuits after 1000 connections: ${circuitSizes.take(10)}..."

    // Multiply the three largest
    def result = 1L
    for (int i = 0; i < Math.min(3, circuitSizes.size()); i++) {
        result *= circuitSizes[i]
    }

    println "Part 1: ${result}"
    return result
}

// Main
def inputFile = args.length > 0 ? args[0] : "input.txt"
solve(inputFile)
