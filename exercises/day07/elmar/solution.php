<?php
// Advent of Code 2025 - Day 7: Laboratories (Part 1)
// Simulate tachyon beams through a manifold and count splits

function solve(string $filename): int {
    $lines = file($filename, FILE_IGNORE_NEW_LINES);
    if ($lines === false) {
        die("Error: Cannot read file $filename\n");
    }

    $grid = [];
    $startCol = -1;
    $height = count($lines);
    $width = 0;

    // Parse grid and find start position 'S'
    foreach ($lines as $row => $line) {
        $grid[$row] = str_split($line);
        $width = max($width, strlen($line));
        $sPos = strpos($line, 'S');
        if ($sPos !== false) {
            $startCol = $sPos;
        }
    }

    if ($startCol === -1) {
        die("Error: No start position 'S' found\n");
    }

    // Track active beams: each beam is [row, col]
    // Beams move downward, row 0 is top
    $splits = 0;

    // Use BFS-like approach: process beams level by level
    // Start beam enters from above row 0 at startCol
    $beams = [$startCol];  // columns of active beams at current row

    for ($row = 0; $row < $height; $row++) {
        if (empty($beams)) break;

        // Remove duplicate beams (merge beams at same column)
        $beams = array_unique($beams);
        $nextBeams = [];

        foreach ($beams as $col) {
            if ($col < 0 || $col >= $width) {
                // Beam exits grid horizontally
                continue;
            }

            $cell = $grid[$row][$col] ?? '.';

            if ($cell === '^') {
                // Splitter: beam stops, emit left and right
                $splits++;
                $nextBeams[] = $col - 1;  // left
                $nextBeams[] = $col + 1;  // right
            } else {
                // Empty space or 'S': beam continues downward
                $nextBeams[] = $col;
            }
        }

        $beams = $nextBeams;
    }

    return $splits;
}

// Main
$filename = $argv[1] ?? 'input.txt';
$result = solve($filename);
echo "Total splits: $result\n";
