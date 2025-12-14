const fs = require("node:fs");

let zeroCounter = 0;
let position = 50;

try {
  const fileData = fs.readFileSync("./input.txt", "utf-8");
  const rows = fileData.split("\n");
  for (let row = 0; row < rows.length; row++) {
    const steps = Number(rows[row].substring(1));

    if (rows[row].startsWith("L")) {
      position -= steps;
    } else {
      position += steps;
    }

    while (position < 0 || position >= 100) {
      if (position < 0) position += 100;
      if (position >= 100) position -= 100;
    }

    if (position === 0) zeroCounter++;
    console.log(
      `${row}: ${rows[row]} - ${steps} = ${position} @ ${zeroCounter}`
    );
  }
  console.log(zeroCounter);
} catch (e) {
  console.error(e);
}
