#!/usr/bin/env perl
# Advent of Code 2025 - Day 9: Movie Theater (Part 2)
# Find largest rectangle using only red and green tiles
# Uses proper scanline with edge direction tracking

use strict;
use warnings;

my $filename = $ARGV[0] // 'input.txt';
open my $fh, '<', $filename or die "Cannot open $filename: $!";

# Parse red tiles in order (they form a connected loop)
my @reds;
while (my $line = <$fh>) {
    chomp $line;
    while ($line =~ /(\d+),(\d+)/g) {
        push @reds, [$1, $2];
    }
}
close $fh;

# Build polygon edges
my @v_edges;  # [x, y_from, y_to] for vertical edges
my @h_edges;  # [y, x_from, x_to] for horizontal edges

for my $i (0 .. $#reds) {
    my $j = ($i + 1) % @reds;
    my ($x1, $y1) = @{$reds[$i]};
    my ($x2, $y2) = @{$reds[$j]};
    
    if ($x1 == $x2) {
        push @v_edges, [$x1, $y1, $y2];
    } else {
        push @h_edges, [$y1, $x1, $x2];
    }
}

# Get valid x-intervals for scanline at row y
sub get_x_intervals {
    my ($y) = @_;
    
    # Get crossings from vertical edges
    my @crossings;
    for my $e (@v_edges) {
        my ($x, $y1, $y2) = @$e;
        my ($ymin, $ymax) = $y1 < $y2 ? ($y1, $y2) : ($y2, $y1);
        if ($ymin <= $y && $y < $ymax) {
            push @crossings, $x;
        }
    }
    
    # Add boundary points from horizontal edges at this y
    for my $e (@h_edges) {
        my ($ey, $x1, $x2) = @$e;
        if ($ey == $y) {
            push @crossings, $x1, $x2;
        }
    }
    
    # Remove duplicates and sort
    my %seen;
    @crossings = sort { $a <=> $b } grep { !$seen{$_}++ } @crossings;
    
    return () if @crossings < 2;
    
    # For a rectilinear polygon, the valid interval at row y spans min to max
    # if the row intersects the polygon interior
    # Build intervals from pairs of crossings
    my @intervals;
    for (my $i = 0; $i < @crossings - 1; $i += 2) {
        push @intervals, [$crossings[$i], $crossings[$i+1]];
    }
    
    # If odd crossings remain, merge with horizontal edge info
    if (@crossings % 2 != 0) {
        # Fallback: use min to max
        return ([$crossings[0], $crossings[-1]]);
    }
    
    return @intervals;
}

# Get all unique y-coordinates from red tiles (sorted)
my @critical_ys = sort { $a <=> $b } keys %{{ map { $_->[1] => 1 } @reds }};

# Cache intervals per y
my %y_intervals;

# Check if x-range [xlo,xhi] is fully covered by valid interval at row y
sub x_range_valid {
    my ($y, $xlo, $xhi) = @_;
    
    unless (exists $y_intervals{$y}) {
        $y_intervals{$y} = [get_x_intervals($y)];
    }
    
    for my $int (@{$y_intervals{$y}}) {
        return 1 if $int->[0] <= $xlo && $xhi <= $int->[1];
    }
    return 0;
}

# Check if rectangle is valid - only check critical y-values
# Between critical y-values, intervals don't change
sub rect_valid {
    my ($x1, $y1, $x2, $y2) = @_;
    my ($xlo, $xhi) = $x1 < $x2 ? ($x1, $x2) : ($x2, $x1);
    my ($ylo, $yhi) = $y1 < $y2 ? ($y1, $y2) : ($y2, $y1);
    
    # Check endpoints
    return 0 unless x_range_valid($ylo, $xlo, $xhi);
    return 0 unless x_range_valid($yhi, $xlo, $xhi);
    
    # Check all critical y-values in range
    for my $y (@critical_ys) {
        next if $y <= $ylo || $y >= $yhi;  # Skip outside/endpoints
        return 0 unless x_range_valid($y, $xlo, $xhi);
    }
    return 1;
}

# Find largest valid rectangle with red corners
my $max_area = 0;
my $n = @reds;

for my $i (0 .. $n - 2) {
    for my $j ($i + 1 .. $n - 1) {
        my ($x1, $y1) = @{$reds[$i]};
        my ($x2, $y2) = @{$reds[$j]};
        
        my $area = (abs($x2 - $x1) + 1) * (abs($y2 - $y1) + 1);
        next if $area <= $max_area;  # Skip if can't beat current max
        
        if (rect_valid($x1, $y1, $x2, $y2)) {
            $max_area = $area;
        }
    }
}

print "Largest valid rectangle area: $max_area\n";
