import * as fs from "fs";

/**
 * Find the maximum 2-digit joltage from a bank of batteries.
 * Pick positions i < j to maximize digit[i]*10 + digit[j].
 * Uses suffix max array for O(n) efficiency.
 */
function maxJoltage(bank: string): number {
  const digits = bank.split("").map(Number);
  const n = digits.length;

  if (n < 2) return 0;

  // Precompute suffix maximum: suffixMax[i] = max digit in positions i..n-1
  const suffixMax: number[] = new Array(n);
  suffixMax[n - 1] = digits[n - 1];
  for (let i = n - 2; i >= 0; i--) {
    suffixMax[i] = Math.max(digits[i], suffixMax[i + 1]);
  }

  // For each position i (first battery), the best second battery is suffix max from i+1
  let maxVal = 0;
  for (let i = 0; i < n - 1; i++) {
    const joltage = digits[i] * 10 + suffixMax[i + 1];
    maxVal = Math.max(maxVal, joltage);
  }

  return maxVal;
}

function solve(filename: string): void {
  const input = fs.readFileSync(filename, "utf-8").trim();
  const banks = input.split("\n").filter((line: string) => line.length > 0);

  let total = 0;
  for (const bank of banks) {
    total += maxJoltage(bank);
  }

  console.log(`Part 1: ${total}`);
}

// Main
const inputFile = process.argv[2] || "input.txt";
solve(inputFile);
