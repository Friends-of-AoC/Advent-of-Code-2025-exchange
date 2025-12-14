# Day 5: Cafeteria

Inventory database with fresh ingredient ID ranges (e.g., `3-5` means IDs 3, 4, 5 are fresh). Ranges can overlap.

## Part 1

**Task:** Count how many available ingredient IDs fall within any fresh range.

Simple range check for each ingredient:
```python
def is_fresh(ingredient_id, ranges):
    for start, end in ranges:
        if start <= ingredient_id <= end:
            return True
    return False
```

## Part 2

**Task:** Count total unique IDs covered by all fresh ranges (ignoring available IDs).

Merge overlapping/adjacent ranges, then sum their sizes:
```python
def merge_ranges(ranges):
    sorted_ranges = sorted(ranges)
    merged = [sorted_ranges[0]]
    for start, end in sorted_ranges[1:]:
        if start <= merged[-1][1] + 1:
            merged[-1] = (merged[-1][0], max(merged[-1][1], end))
        else:
            merged.append((start, end))
    return merged

total = sum(end - start + 1 for start, end in merged)
```

Example: `3-5`, `10-14`, `16-20`, `12-18` → merged: `3-5`, `10-20` → **14** IDs.

## Usage

```bash
python3 solution.py input.txt        # Part 1
python3 solution_part2.py input.txt  # Part 2
```

Requires Python 3.6+.
