#!/usr/bin/env ruby
# Advent of Code 2025 - Day 12: Christmas Tree Farm
require 'set'
require 'timeout'

# Parse a shape from array of row strings
def parse_shape_lines(lines)
  cells = []
  lines.each_with_index do |row, r|
    row.strip.chars.each_with_index { |ch, c| cells << [r, c] if ch == '#' }
  end
  normalize(cells)
end

def normalize(cells)
  return [] if cells.empty?
  min_r = cells.map(&:first).min
  min_c = cells.map(&:last).min
  cells.map { |r, c| [r - min_r, c - min_c] }.sort
end

def all_orientations(cells)
  return [[]] if cells.empty?
  orientations = Set.new
  current = cells.dup
  4.times do
    orientations << normalize(current)
    orientations << normalize(current.map { |r, c| [r, -c] })
    current = current.map { |r, c| [c, -r] }
  end
  orientations.to_a
end

# Precompute all valid placements as bitmasks
def valid_placements(orientations, h, w)
  placements = []
  orientations.each do |cells|
    max_r = cells.map(&:first).max
    max_c = cells.map(&:last).max
    (0..(h - 1 - max_r)).each do |r|
      (0..(w - 1 - max_c)).each do |c|
        mask = cells.map { |dr, dc| (r + dr) * w + (c + dc) }
        placements << mask
      end
    end
  end
  placements.uniq
end

$timeout_flag = false

def solve_bitmask(used, placements_list, idx)
  return false if $timeout_flag
  return true if idx >= placements_list.size

  placements_list[idx].each do |mask|
    next if mask.any? { |bit| used[bit] }
    mask.each { |bit| used[bit] = true }
    return true if solve_bitmask(used, placements_list, idx + 1)
    mask.each { |bit| used[bit] = false }
  end
  false
end

def can_fit?(width, height, shapes, quantities)
  shapes_to_place = []
  quantities.each_with_index do |qty, idx|
    next if shapes[idx].nil? || shapes[idx].empty? || shapes[idx] == [[]]
    qty.times { shapes_to_place << shapes[idx] }
  end

  return true if shapes_to_place.empty?

  total_cells = shapes_to_place.sum { |orients| orients.first.size }
  grid_size = width * height
  return false if total_cells > grid_size

  placements_list = shapes_to_place.map { |orients| valid_placements(orients, height, width) }
  return false if placements_list.any?(&:empty?)
  placements_list.sort_by!(&:size)

  $timeout_flag = false
  used = Array.new(grid_size, false)
  
  begin
    Timeout.timeout(0.1) { solve_bitmask(used, placements_list, 0) }
  rescue Timeout::Error
    $timeout_flag = true
    false
  end
end

def solve_puzzle(filename)
  input = File.read(filename)
  blocks = input.strip.split(/\n\n+/)

  shapes = {}
  regions = []

  blocks.each do |block|
    lines = block.strip.split("\n")
    first = lines.first.strip

    if first =~ /^(\d+):(.*)$/
      idx = Regexp.last_match(1).to_i
      rest = Regexp.last_match(2).strip
      shape_lines = rest.empty? ? lines[1..] : [rest] + lines[1..]
      shapes[idx] = all_orientations(parse_shape_lines(shape_lines))
    else
      lines.each do |line|
        if line =~ /^(\d+)x(\d+):\s*(.*)$/
          w = Regexp.last_match(1).to_i
          h = Regexp.last_match(2).to_i
          q = Regexp.last_match(3).split.map(&:to_i)
          regions << [w, h, q]
        end
      end
    end
  end

  max_idx = shapes.keys.max || -1
  shapes_array = (0..max_idx).map { |i| shapes[i] || [[]] }

  count = 0
  regions.each_with_index do |(w, h, q), i|
    STDERR.print "\r#{i+1}/#{regions.size}" if regions.size > 10
    count += 1 if can_fit?(w, h, shapes_array, q)
  end
  STDERR.puts if regions.size > 10
  count
end

# Main
filename = ARGV[0] || "input.txt"
result = solve_puzzle(filename)
puts "Part 1: #{result}"
