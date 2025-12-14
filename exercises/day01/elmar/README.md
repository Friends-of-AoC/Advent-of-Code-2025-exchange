# Day 1: Secret Entrance

A safe with a circular dial (0-99) needs to be cracked. The dial starts at 50 and follows rotation commands like `L68` (left 68 clicks) or `R48` (right 48 clicks).

## Part 1

**Task:** Count rotations that **end** on 0.

Simple modulo arithmetic handles the circular wrap-around:
```lua
dial = (dial + distance) % 100  -- right
dial = (dial - distance) % 100  -- left
```

## Part 2

**Task:** Count **every click** that lands on 0 — not just the final position.

Example: `R1000` from position 50 passes through 0 exactly 10 times.

The trick is calculating crossings mathematically instead of simulating each click:
- **Right:** `floor((position + distance) / 100)` — counts wrap-arounds past 99→0
- **Left:** First hit after `position` clicks, then every 100 clicks

## Usage

```bash
lua solution.lua input.txt       # Part 1
lua solution_part2.lua input.txt # Part 2
```

Requires Lua 5.x (`sudo apt install lua5.4` on Ubuntu/Debian).
