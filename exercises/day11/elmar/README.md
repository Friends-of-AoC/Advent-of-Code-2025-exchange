# Day 11: Reactor

A directed graph of devices connects power from `you` to `out`. Count all distinct paths.

## Part 1

**Task:** Count all paths from `you` to `out`.

Uses iterative DFS to enumerate all paths. Each node can be visited multiple times (different paths may share nodes).

```
you: bbb ccc
bbb: ddd eee
ccc: eee fff
```

This means device `you` outputs to `bbb` and `ccc`, etc.

## Part 2

**Task:** Count paths from `svr` to `out` that pass through both `dac` and `fft`.

Same DFS approach, but tracks whether each required node has been visited along the current path.

## Usage

```bash
scala-cli solution.scala -- test.txt         # Part 1 test (expect: 5)
scala-cli solution.scala -- input.txt        # Part 1
scala-cli solution_part2.scala -- test2.txt  # Part 2 test (expect: 2)
scala-cli solution_part2.scala -- input.txt  # Part 2
```

Requires scala-cli.
