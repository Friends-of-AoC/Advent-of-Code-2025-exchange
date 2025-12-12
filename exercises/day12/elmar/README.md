# Day 12: Christmas Tree Farm

Pack presents (polyomino shapes) into rectangular regions under Christmas trees.

## Part 1

**Task:** Count how many regions can fit all their required presents.

Each present shape can be rotated and flipped. Shapes can't overlap but can fit together (only `#` cells occupy space).

### Approach

- **Shape parsing:** Convert multi-line shape definitions to coordinate sets
- **Orientations:** Generate all 8 rotations/reflections for each shape
- **Precomputation:** Calculate all valid placements for each shape in each grid
- **Backtracking:** Bitmask-based solver with most-constrained-first ordering

## Part 2

Story only - no additional puzzle. Solving Part 1 earns the star.

## Usage

```bash
ruby solution.rb input.txt       # Part 1
```

Requires Ruby 2.5+ (`sudo apt install ruby` on Ubuntu/Debian).

## Example

Test input expects result: `2` (2 of 3 regions can fit their presents).
