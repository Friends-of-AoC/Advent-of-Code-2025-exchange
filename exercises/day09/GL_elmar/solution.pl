#!/usr/bin/env perl
# Advent of Code 2025 - Day 9: Movie Theater (Part 1)
# Find largest rectangle using two red tiles as opposite corners

use strict;
use warnings;

my $filename = $ARGV[0] // 'input.txt';
open my $fh, '<', $filename or die "Cannot open $filename: $!";

my @tiles;
while (my $line = <$fh>) {
    chomp $line;
    # Parse space-separated coordinates like "7,1 11,1 9,7"
    while ($line =~ /(\d+),(\d+)/g) {
        push @tiles, [$1, $2];
    }
}
close $fh;

my $max_area = 0;

# Check all pairs of tiles as opposite corners
for my $i (0 .. $#tiles - 1) {
    for my $j ($i + 1 .. $#tiles) {
        my ($x1, $y1) = @{$tiles[$i]};
        my ($x2, $y2) = @{$tiles[$j]};
        
        # Rectangle includes both corners, so add 1 to each dimension
        my $area = (abs($x2 - $x1) + 1) * (abs($y2 - $y1) + 1);
        $max_area = $area if $area > $max_area;
    }
}

print "Largest rectangle area: $max_area\n";
