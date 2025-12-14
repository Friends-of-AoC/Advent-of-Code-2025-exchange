# Day 10: Factory

Factory machines need initialization. Two modes of operation:
1. **Part 1:** Toggle indicator lights (XOR) - find minimum presses
2. **Part 2:** Increment joltage counters (addition) - find minimum presses

## Part 1

Each button toggles specific lights. Since toggling twice cancels out, each button is pressed 0 or 1 times. Brute-force all `2^n` combinations using bitmasks.

## Part 2

Each button adds 1 to specific counters. Need exact target values with minimum total presses.

**Approach:** Solve as linear algebra problem over rationals:
1. Build matrix A where A[i][j] = 1 if button j affects counter i
2. Gaussian elimination to RREF
3. Extract particular solution and null space basis
4. Search over free variables to find minimum L1-norm non-negative integer solution

## Usage

```bash
cargo run --release -- test.txt   # Test (Part 1: 7, Part 2: 33)
cargo run --release -- input.txt  # Puzzle input
```

Requires Rust toolchain (`rustup` from https://rustup.rs).
