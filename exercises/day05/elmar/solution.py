#!/usr/bin/env python3
"""Advent of Code 2025 - Day 5: Cafeteria (Part 1)"""

import sys


def parse_input(filename):
    """Parse fresh ranges and available ingredient IDs from input file."""
    with open(filename) as f:
        content = f.read().strip()
    
    parts = content.split('\n\n')
    
    # Parse ranges (format: "start-end")
    ranges = []
    for line in parts[0].split('\n'):
        for r in line.split():
            start, end = map(int, r.split('-'))
            ranges.append((start, end))
    
    # Parse available ingredient IDs
    ingredients = []
    for line in parts[1].split('\n'):
        ingredients.extend(map(int, line.split()))
    
    return ranges, ingredients


def is_fresh(ingredient_id, ranges):
    """Check if ingredient ID falls within any fresh range."""
    for start, end in ranges:
        if start <= ingredient_id <= end:
            return True
    return False


def solve(filename):
    """Count how many available ingredient IDs are fresh."""
    ranges, ingredients = parse_input(filename)
    
    fresh_count = sum(1 for ing in ingredients if is_fresh(ing, ranges))
    return fresh_count


if __name__ == "__main__":
    filename = sys.argv[1] if len(sys.argv) > 1 else "input.txt"
    result = solve(filename)
    print(f"Fresh ingredients: {result}")
