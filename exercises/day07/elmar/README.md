# Day 7: Laboratories

A tachyon beam enters a manifold at position `S` and moves downward. Splitters (`^`) stop the beam and emit two new beams (left and right). Empty space (`.`) allows free passage.

## Part 1

**Task:** Count how many times the beam is split.

Simulation approach — process beams row by row:
```php
foreach ($beams as $col) {
    if ($cell === '^') {
        $splits++;
        $nextBeams[] = $col - 1;  // left
        $nextBeams[] = $col + 1;  // right
    } else {
        $nextBeams[] = $col;      // continue down
    }
}
```

Beams at the same column merge (deduplicated each row).

## Part 2

**Task:** Count distinct timelines (many-worlds interpretation).

Each splitter creates 2 timelines — one where the particle went left, one right. Track particle counts per column without merging:
```php
if ($cell === '^') {
    $nextCounts[$col - 1] += $count;  // left timeline(s)
    $nextCounts[$col + 1] += $count;  // right timeline(s)
}
```

Final answer = sum of all particle counts at the bottom.

## Usage

```bash
php solution.php input.txt        # Part 1
php solution_part2.php input.txt  # Part 2
```

Requires PHP 7.x+ (`sudo apt install php-cli` on Ubuntu/Debian).
