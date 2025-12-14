# Day 2: Gift Shop

Invalid product IDs were added to the gift shop database. Invalid IDs follow silly digit patterns — sequences repeated multiple times. Given ID ranges, find and sum all invalid IDs.

## Part 1

**Task:** Find IDs made of a sequence repeated **exactly twice**.

Examples: `55`, `6464`, `123123`

Simple string check — split in half and compare:
```go
half := len(s) / 2
return s[:half] == s[half:]  // "6464" → "64" == "64" ✓
```

## Part 2

**Task:** Find IDs made of a sequence repeated **at least twice**.

Examples: `111` (1×3), `123123123` (123×3), `1212121212` (12×5)

Try all possible pattern lengths from 1 to len/2:
```go
for patLen := 1; patLen <= length/2; patLen++ {
    if length%patLen != 0 { continue }
    // Check if s consists of s[:patLen] repeated
}
```

## Usage

```bash
go run solution.go input.txt        # Part 1
go run solution_part2.go input.txt  # Part 2
```
