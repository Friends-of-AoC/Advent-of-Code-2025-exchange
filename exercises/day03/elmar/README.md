# Day 3: Lobby

Battery banks power an offline escalator. Each bank is a string of digits (1-9). Pick exactly two batteries (positions i < j) to form a 2-digit joltage. Find the maximum joltage per bank and sum them.

## Part 1

**Task:** Find the largest 2-digit number from each bank by selecting two battery positions.

Algorithm uses suffix maximum for O(n) per bank:
```typescript
// suffixMax[i] = max digit in positions i..n-1
for (let i = 0; i < n - 1; i++) {
  joltage = digits[i] * 10 + suffixMax[i + 1];
}
```

Example: `818181911112111` → first 9 at pos 6, max after is 2 → **92**

## Part 2

**Task:** Select exactly **12 batteries** per bank to form the largest 12-digit number.

Greedy approach — at each step, pick the largest digit that leaves enough remaining:
```typescript
for (let remaining = k; remaining > 0; remaining--) {
  // Pick max digit from [start, n-remaining]
  // Ensures enough digits left for remaining picks
}
```

Example: `234234234234278` → skip early 2,3,2 → **434234234278**

## Usage

```bash
npx ts-node solution.ts input.txt        # Part 1
npx ts-node solution_part2.ts input.txt  # Part 2
```

Or compile first:
```bash
npx tsc solution.ts && node solution.js input.txt
```

Requires Node.js and TypeScript (`npm install -g typescript ts-node`).
