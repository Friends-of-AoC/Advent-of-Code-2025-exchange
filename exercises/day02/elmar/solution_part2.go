package main

import (
	"fmt"
	"os"
	"strconv"
	"strings"
)

// isInvalid checks if a number is made of a sequence repeated at least twice
// e.g., 1111111 (1 seven times), 123123123 (123 three times)
func isInvalid(n int64) bool {
	s := strconv.FormatInt(n, 10)
	length := len(s)

	// Try all possible pattern lengths from 1 to length/2
	for patLen := 1; patLen <= length/2; patLen++ {
		if length%patLen != 0 {
			continue
		}

		pattern := s[:patLen]
		valid := true
		for i := patLen; i < length; i += patLen {
			if s[i:i+patLen] != pattern {
				valid = false
				break
			}
		}
		if valid {
			return true
		}
	}
	return false
}

func main() {
	if len(os.Args) < 2 {
		fmt.Println("Usage: go run solution_part2.go <input.txt>")
		os.Exit(1)
	}

	data, err := os.ReadFile(os.Args[1])
	if err != nil {
		fmt.Fprintf(os.Stderr, "Error: %v\n", err)
		os.Exit(1)
	}

	input := strings.TrimSpace(string(data))
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

	fmt.Printf("Part 2: %d (%d invalid IDs)\n", totalSum, count)
}
