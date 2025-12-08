// Advent of Code 2025 - Day 8: Playground (Part 2)
// Connect until all in one circuit, multiply X coords of last connection

class UnionFind2 {
    int[] parent
    int[] rank
    int numComponents

    UnionFind2(int n) {
        parent = new int[n]
        rank = new int[n]
        numComponents = n
        for (int i = 0; i < n; i++) {
            parent[i] = i
            rank[i] = 0
        }
    }

    int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x])
        }
        return parent[x]
    }

    boolean union(int x, int y) {
        int rootX = find(x)
        int rootY = find(y)
        if (rootX == rootY) return false

        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX
        } else {
            parent[rootY] = rootX
            rank[rootX]++
        }
        numComponents--
        return true
    }

    boolean allConnected() {
        return numComponents == 1
    }
}

def solve(String filename) {
    def lines = new File(filename).readLines().findAll { it.trim() }
    
    def boxes = lines.collect { line ->
        def parts = line.split(',').collect { it.trim().toLong() }
        [x: parts[0], y: parts[1], z: parts[2]]
    }
    
    int n = boxes.size()
    println "Loaded ${n} junction boxes"

    def pairs = []
    for (int i = 0; i < n; i++) {
        for (int j = i + 1; j < n; j++) {
            def dx = boxes[i].x - boxes[j].x
            def dy = boxes[i].y - boxes[j].y
            def dz = boxes[i].z - boxes[j].z
            def distSq = dx*dx + dy*dy + dz*dz
            pairs << [i: i, j: j, distSq: distSq]
        }
    }

    pairs.sort { it.distSq }
    println "Calculated ${pairs.size()} pairs"

    def uf = new UnionFind2(n)
    def lastPair = null
    int connections = 0

    for (def pair : pairs) {
        if (uf.union(pair.i, pair.j)) {
            connections++
            lastPair = pair
            if (uf.allConnected()) break
        }
    }

    def boxA = boxes[lastPair.i]
    def boxB = boxes[lastPair.j]
    def result = boxA.x * boxB.x

    println "Last connection: (${boxA.x},${boxA.y},${boxA.z}) <-> (${boxB.x},${boxB.y},${boxB.z})"
    println "Connections made: ${connections}"
    println "Part 2: ${boxA.x} * ${boxB.x} = ${result}"
    return result
}

def inputFile = args.length > 0 ? args[0] : "input.txt"
solve(inputFile)
