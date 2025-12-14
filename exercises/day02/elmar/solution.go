package main

import (
	"fmt"
	"os"
	"strconv"
	"strings"
)

// isInvalid checks if a number is made of a sequence repeated exactly twice
// e.g., 55 (5+5), 6464 (64+64), 123123 (123+123)
func isInvalid(n int64) bool {
	s := strconv.FormatInt(n, 10)
	length := len(s)

	// Must have even length to be a repeated sequence
	if length%2 != 0 {
		return false
	}

	half := length / 2
	return s[:half] == s[half:]
}

func main() {
	if len(os.Args) < 2 {
		fmt.Println("Usage: go run solution.go <input.txt>")
		os.Exit(1)
	}

	data, err := os.ReadFile(os.Args[1])
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error: %v\n", err)
		os.Exit(1)
	}

	input := strings.TrimSpace(string(data))
	// Remove any whitespace/newlines within the input
	input = strings.ReplaceAll(input, " ", "")
	input = strings.ReplaceAll(input, "\n", "")

	ranges := strings.Split(input, ",")

	var totalSum int64
	var count int

	for _, r := range ranges {
		parts := strings.Split(r, "-")
		if len(parts) != 2 {
			continue
		}

		start, _ := strconv.ParseInt(parts[0], 10, 64)
		end, _ := strconv.ParseInt(parts[1], 10, 64)

		for id := start; id <= end; id++ {
			if isInvalid(id) {
				totalSum += id
				count++
			}
		}
	}

	fmt.Printf("Part 1: %d (%d invalid IDs)\n", totalSum, count)
}
