#!/usr/bin/env python3
"""Advent of Code 2025 - Day 5: Cafeteria (Part 2)"""

import sys


def parse_ranges(filename):
    """Parse fresh ranges from input file (ignore available IDs)."""
    with open(filename) as f:
        content = f.read().strip()
    
    parts = content.split('\n\n')
    
    # Parse ranges (format: "start-end")
    ranges = []
    for line in parts[0].split('\n'):
        for r in line.split():
            start, end = map(int, r.split('-'))
            ranges.append((start, end))
    
    return ranges


def merge_ranges(ranges):
    """Merge overlapping ranges to count unique IDs."""
    if not ranges:
        return []
    
    # Sort by start position
    sorted_ranges = sorted(ranges)
    merged = [sorted_ranges[0]]
    
    for start, end in sorted_ranges[1:]:
        last_start, last_end = merged[-1]
        
        # Check if ranges overlap or are adjacent
        if start <= last_end + 1:
            # Extend the last range
            merged[-1] = (last_start, max(last_end, end))
        else:
            merged.append((start, end))
    
    return merged


def count_fresh_ids(ranges):
    """Count total unique fresh ingredient IDs."""
    merged = merge_ranges(ranges)
    return sum(end - start + 1 for start, end in merged)


def solve(filename):
    """Count how many ingredient IDs are considered fresh."""
    ranges = parse_ranges(filename)
    return count_fresh_ids(ranges)


if __name__ == "__main__":
    filename = sys.argv[1] if len(sys.argv) > 1 else "input.txt"
    result = solve(filename)
    print(f"Total fresh ingredient IDs: {result}")
