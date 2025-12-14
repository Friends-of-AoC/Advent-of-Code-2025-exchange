package main

import (
	"bufio"
	"fmt"
	"os"
	"strconv"
	"strings"
)

type Shape [][]int // list of orientations, each orientation is list of cell indices

// Parse shape from lines into cell coordinates
func parseShape(lines []string) [][2]int {
	var cells [][2]int
	for r, line := range lines {
		for c, ch := range line {
			if ch == '#' {
				cells = append(cells, [2]int{r, c})
			}
		}
	}
	return normalize(cells)
}

// Normalize so min row/col is 0
func normalize(cells [][2]int) [][2]int {
	if len(cells) == 0 {
		return cells
	}
	minR, minC := cells[0][0], cells[0][1]
	for _, c := range cells {
		if c[0] < minR {
			minR = c[0]
		}
		if c[1] < minC {
			minC = c[1]
		}
	}
	result := make([][2]int, len(cells))
	for i, c := range cells {
		result[i] = [2]int{c[0] - minR, c[1] - minC}
	}
	return result
}

// Generate all 8 orientations (4 rotations × 2 flips)
func allOrientations(cells [][2]int) [][][2]int {
	seen := make(map[string]bool)
	var result [][][2]int

	current := cells
	for i := 0; i < 4; i++ {
		// Original
		norm := normalize(current)
		key := fmt.Sprintf("%v", norm)
		if !seen[key] {
			seen[key] = true
			result = append(result, norm)
		}

		// Flipped
		flipped := make([][2]int, len(current))
		for j, c := range current {
			flipped[j] = [2]int{c[0], -c[1]}
		}
		norm = normalize(flipped)
		key = fmt.Sprintf("%v", norm)
		if !seen[key] {
			seen[key] = true
			result = append(result, norm)
		}

		// Rotate 90°
		rotated := make([][2]int, len(current))
		for j, c := range current {
			rotated[j] = [2]int{c[1], -c[0]}
		}
		current = rotated
	}
	return result
}

// Precompute valid placements as bitmasks
func validPlacements(orientations [][][2]int, h, w int) []uint64 {
	seen := make(map[uint64]bool)
	var placements []uint64

	for _, cells := range orientations {
		maxR, maxC := 0, 0
		for _, c := range cells {
			if c[0] > maxR {
				maxR = c[0]
			}
			if c[1] > maxC {
				maxC = c[1]
			}
		}

		for r := 0; r <= h-1-maxR; r++ {
			for c := 0; c <= w-1-maxC; c++ {
				var mask uint64
				for _, cell := range cells {
					bit := (r+cell[0])*w + (c + cell[1])
					mask |= 1 << bit
				}
				if !seen[mask] {
					seen[mask] = true
					placements = append(placements, mask)
				}
			}
		}
	}
	return placements
}

// Backtracking solver with bitmask
func solve(used uint64, placements [][]uint64, idx int) bool {
	if idx >= len(placements) {
		return true
	}

	for _, mask := range placements[idx] {
		if used&mask == 0 {
			if solve(used|mask, placements, idx+1) {
				return true
			}
		}
	}
	return false
}

// Check if shapes fit in region
func canFit(w, h int, shapes [][][][2]int, quantities []int) bool {
	var toPlace [][][][2]int
	totalCells := 0

	for idx, qty := range quantities {
		if idx >= len(shapes) || len(shapes[idx]) == 0 {
			continue
		}
		cellCount := len(shapes[idx][0])
		totalCells += qty * cellCount
		for i := 0; i < qty; i++ {
			toPlace = append(toPlace, shapes[idx])
		}
	}

	if len(toPlace) == 0 {
		return true
	}

	gridSize := w * h
	if totalCells > gridSize {
		return false
	}

	// For grids > 64 cells, fall back to area check only
	if gridSize > 64 {
		return true
	}

	// Compute placements for each shape
	var placementsList [][]uint64
	for _, shapeOrientations := range toPlace {
		p := validPlacements(shapeOrientations, h, w)
		if len(p) == 0 {
			return false
		}
		placementsList = append(placementsList, p)
	}

	// Sort by fewest placements first
	for i := 0; i < len(placementsList)-1; i++ {
		for j := i + 1; j < len(placementsList); j++ {
			if len(placementsList[j]) < len(placementsList[i]) {
				placementsList[i], placementsList[j] = placementsList[j], placementsList[i]
			}
		}
	}

	return solve(0, placementsList, 0)
}

func main() {
	filename := "input.txt"
	if len(os.Args) > 1 {
		filename = os.Args[1]
	}

	file, err := os.Open(filename)
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error: %v\n", err)
		os.Exit(1)
	}
	defer file.Close()

	var shapes [][][][2]int
	var regions [][3]interface{} // [w, h, quantities]

	scanner := bufio.NewScanner(file)
	var currentLines []string
	var currentIdx int = -1

	for scanner.Scan() {
		line := scanner.Text()

		if line == "" {
			if currentIdx >= 0 && len(currentLines) > 0 {
				// Extend shapes slice if needed
				for len(shapes) <= currentIdx {
					shapes = append(shapes, nil)
				}
				shapes[currentIdx] = allOrientations(parseShape(currentLines))
			}
			currentIdx = -1
			currentLines = nil
			continue
		}

		// Shape definition?
		if len(line) > 0 && line[0] >= '0' && line[0] <= '9' && strings.Contains(line, ":") {
			parts := strings.SplitN(line, ":", 2)
			idx, _ := strconv.Atoi(strings.TrimSpace(parts[0]))

			// Check if it's a region (WxH:)
			if strings.Contains(parts[0], "x") {
				dims := strings.Split(parts[0], "x")
				w, _ := strconv.Atoi(dims[0])
				h, _ := strconv.Atoi(dims[1])
				qParts := strings.Fields(strings.TrimSpace(parts[1]))
				var quantities []int
				for _, q := range qParts {
					n, _ := strconv.Atoi(q)
					quantities = append(quantities, n)
				}
				regions = append(regions, [3]interface{}{w, h, quantities})
			} else {
				// Shape definition
				currentIdx = idx
				rest := strings.TrimSpace(parts[1])
				if rest != "" {
					currentLines = []string{rest}
				} else {
					currentLines = nil
				}
			}
		} else if currentIdx >= 0 {
			currentLines = append(currentLines, line)
		} else if strings.Contains(line, "x") && strings.Contains(line, ":") {
			// Region on its own line
			parts := strings.SplitN(line, ":", 2)
			dims := strings.Split(strings.TrimSpace(parts[0]), "x")
			w, _ := strconv.Atoi(dims[0])
			h, _ := strconv.Atoi(dims[1])
			qParts := strings.Fields(strings.TrimSpace(parts[1]))
			var quantities []int
			for _, q := range qParts {
				n, _ := strconv.Atoi(q)
				quantities = append(quantities, n)
			}
			regions = append(regions, [3]interface{}{w, h, quantities})
		}
	}

	// Don't forget last shape
	if currentIdx >= 0 && len(currentLines) > 0 {
		for len(shapes) <= currentIdx {
			shapes = append(shapes, nil)
		}
		shapes[currentIdx] = allOrientations(parseShape(currentLines))
	}

	count := 0
	for i, region := range regions {
		w := region[0].(int)
		h := region[1].(int)
		q := region[2].([]int)

		if len(regions) > 10 {
			fmt.Fprintf(os.Stderr, "\r%d/%d", i+1, len(regions))
		}

		if canFit(w, h, shapes, q) {
			count++
		}
	}

	if len(regions) > 10 {
		fmt.Fprintln(os.Stderr)
	}

	fmt.Printf("Part 1: %d\n", count)
}
