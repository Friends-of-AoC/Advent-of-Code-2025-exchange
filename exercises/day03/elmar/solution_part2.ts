import * as fs from "fs";

/**
 * Find the largest k-digit number by selecting k positions from the bank.
 * Greedy approach: at each step, pick the largest digit that still leaves
 * enough digits remaining to complete the selection.
 */
function maxJoltageK(bank: string, k: number): bigint {
  const n = bank.length;
  if (n < k) return 0n;

  const result: string[] = [];
  let start = 0;

  for (let remaining = k; remaining > 0; remaining--) {
    // We can pick from start to (n - remaining) inclusive
    const end = n - remaining;
    let bestIdx = start;
    let bestDigit = bank[start];

    for (let i = start + 1; i <= end; i++) {
      if (bank[i] > bestDigit) {
        bestDigit = bank[i];
        bestIdx = i;
      }
    }

    result.push(bestDigit);
    start = bestIdx + 1;
  }

  return BigInt(result.join(""));
}

function solve(filename: string): void {
  const input = fs.readFileSync(filename, "utf-8").trim();
  const banks = input.split("\n").filter((line: string) => line.length > 0);

  let total1 = 0n;
  let total2 = 0n;

  for (const bank of banks) {
    total1 += maxJoltageK(bank, 2);
    total2 += maxJoltageK(bank, 12);
  }

  console.log(`Part 1: ${total1}`);
  console.log(`Part 2: ${total2}`);
}

// Main
const inputFile = process.argv[2] || "input.txt";
solve(inputFile);
