// Advent of Code 2025 - Day 11: Reactor (Part 2)

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

  /** Count paths from start to end that visit both required nodes (memoized) */
  def countPathsWithBoth(
      graph: Map[String, List[String]],
      start: String,
      end: String,
      required1: String,
      required2: String
  ): Long = {
    // Memoization: (node, hasVisited1, hasVisited2) -> path count
    val memo = mutable.Map[(String, Boolean, Boolean), Long]()

    def dfs(node: String, visited1: Boolean, visited2: Boolean): Long = {
      val v1 = visited1 || (node == required1)
      val v2 = visited2 || (node == required2)

      if (node == end) {
        return if (v1 && v2) 1L else 0L
      }

      val key = (node, v1, v2)
      memo.getOrElseUpdate(key, {
        graph(node).map(child => dfs(child, v1, v2)).sum
      })
    }

    dfs(start, visited1 = false, visited2 = false)
  }

  def solve(filename: String): Long = {
    val lines = Source.fromFile(filename).getLines().toSeq
    val graph = parseGraph(lines)
    countPathsWithBoth(graph, "svr", "out", "dac", "fft")
  }

  def main(args: Array[String]): Unit = {
    val filename = if (args.length > 0) args(0) else "input.txt"
    val result = solve(filename)
    println(s"Part 2: $result")
  }
}
