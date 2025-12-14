# Day 9: Movie Theater

Find the largest rectangle using red tiles as opposite corners.

## Part 1

**Task:** Given coordinates of red tiles, find the maximum rectangle area where any two red tiles form opposite corners.

The area includes both corner tiles: `(|x2-x1| + 1) * (|y2-y1| + 1)`.

Simple O(n²) approach: check all pairs of tiles.

## Part 2

**Task:** Rectangle must only contain red or green tiles. Green tiles are:
- Lines connecting consecutive red tiles (the polygon boundary)
- All tiles inside the closed polygon

Uses scanline algorithm to compute valid x-intervals per row. Only checks "critical" y-values (where polygon vertices exist) for efficiency.

## Usage

```bash
perl solution.pl test.txt        # Part 1 test (expect: 50)
perl solution.pl input.txt       # Part 1
perl solution_part2.pl test.txt  # Part 2 test (expect: 24)
perl solution_part2.pl input.txt # Part 2
```

Requires Perl 5.x (usually pre-installed on Linux/macOS).
