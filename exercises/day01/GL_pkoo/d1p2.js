const fs = require("node:fs");

let zeroCounter = 0;
let position = 50;

try {
  const fileData = fs.readFileSync("./input.txt", "utf-8");
  const rows = fileData.split("\n");

  for (let row = 0; row < rows.length; row++) {
    const direction = rows[row][0];
    const steps = Number(rows[row].substring(1));

    for (let i = 0; i < steps; i++) {
      if (direction === "L") {
        position = (position - 1 + 100) % 100;
      } else {
        position = (position + 1) % 100;
      }

      if (position === 0) {
        zeroCounter++;
      }
    }

    console.log(
      `${row}: ${rows[row]} ends at ${position} with zeros counted: ${zeroCounter}`
    );
  }
  console.log("Total zero counts (method 0x434C49434B):", zeroCounter);
} catch (e) {
  console.error(e);
}
