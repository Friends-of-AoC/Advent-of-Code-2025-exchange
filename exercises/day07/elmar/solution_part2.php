<?php
// Advent of Code 2025 - Day 7: Laboratories (Part 2)
// Count distinct timelines (many-worlds interpretation)
// Each splitter creates 2 separate timelines

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

    // Track particle counts per column (not deduplicated!)
    // Each value represents number of particles/timelines at that column
    $beamCounts = [$startCol => 1];

    for ($row = 0; $row < $height; $row++) {
        if (empty($beamCounts)) break;

        $nextCounts = [];

        foreach ($beamCounts as $col => $count) {
            if ($col < 0 || $col >= $width) {
                // Particles exit grid - they still count as timelines
                // but don't propagate further
                continue;
            }

            $cell = $grid[$row][$col] ?? '.';

            if ($cell === '^') {
                // Splitter: each particle becomes 2 (left and right)
                $left = $col - 1;
                $right = $col + 1;
                $nextCounts[$left] = ($nextCounts[$left] ?? 0) + $count;
                $nextCounts[$right] = ($nextCounts[$right] ?? 0) + $count;
            } else {
                // Empty space or 'S': particles continue downward
                $nextCounts[$col] = ($nextCounts[$col] ?? 0) + $count;
            }
        }

        $beamCounts = $nextCounts;
    }

    // Sum all particles = total number of distinct timelines
    return array_sum($beamCounts);
}

// Main
$filename = $argv[1] ?? 'input.txt';
$result = solve($filename);
echo "Total timelines: $result\n";
