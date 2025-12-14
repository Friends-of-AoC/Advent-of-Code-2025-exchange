// Advent of Code 2025 - Day 11: Reactor

import scala.io.Source
import scala.collection.mutable

object Solution {

  /** Parse input into adjacency list: device -> list of output devices */
  def parseGraph(lines: Seq[String]): Map[String, List[String]] = {
    lines
      .filter(_.contains(":"))
      .map { line =>
        val parts = line.split(":")
        val device = parts(0).trim
        val outputs = if (parts.length > 1 && parts(1).trim.nonEmpty) {
          parts(1).trim.split("\\s+").toList
        } else {
          List.empty
        }
        device -> outputs
      }
      .toMap
      .withDefaultValue(List.empty)
  }

  /** Count all paths from 'start' to 'end' using DFS */
  def countPaths(graph: Map[String, List[String]], start: String, end: String): Long = {
    if (start == end) return 1L

    var count = 0L
    val stack = mutable.Stack[(String, Int)]() // (node, index into children)

    stack.push((start, 0))

    while (stack.nonEmpty) {
      val (node, idx) = stack.pop()
      val children = graph(node)

      if (idx < children.length) {
        stack.push((node, idx + 1))
        val child = children(idx)
        
        if (child == end) {
          count += 1
        } else {
          stack.push((child, 0))
        }
      }
    }

    count
  }

  def solve(filename: String): Long = {
    val lines = Source.fromFile(filename).getLines().toSeq
    val graph = parseGraph(lines)
    countPaths(graph, "you", "out")
  }

  def main(args: Array[String]): Unit = {
    val filename = if (args.length > 0) args(0) else "input.txt"
    val result = solve(filename)
    println(s"Part 1: $result")
  }
}
