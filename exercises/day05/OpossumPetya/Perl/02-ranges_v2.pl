use strict;
use warnings;
use feature 'say';
use List::Util qw(max min); # core module

sub listsOverlap {
    my ($a, $b) = @_;
    my $low = max($a->[0], $b->[0]);
    my $high = min($a->[1], $b->[1]);
    return $low <= $high;
}

my @ranges;

my $fh;
if (@ARGV) {
    open $fh, '<', $ARGV[0] or die "Can't open file '$ARGV[0]': $!";
} else {
    $fh = *DATA;
}

while (my $row = <$fh>) {
    chomp $row;                     # drop newline
    last unless $row;               # stop at the empty line

    my @rangeLimits = split /\-/, $row ;
    push @ranges, [ @rangeLimits] ;
}
close $fh;

# sort ranges by the lower limit
my @sorted_ranges = sort { $a->[0] <=> $b->[0] } @ranges ;

my @final_ranges;
push @final_ranges, shift @sorted_ranges; # take first as is to start with

my $pos = 0;    # pointer to current index of @sorted_ranges
my $end = scalar @sorted_ranges;
while ($pos < $end) {
    # if lists overlap, expand the current range in the "final ranges" array
    if (listsOverlap($sorted_ranges[$pos], $final_ranges[$#final_ranges])) {
        $final_ranges[$#final_ranges]->[0] = min($sorted_ranges[$pos]->[0], $final_ranges[$#final_ranges][0]);
        $final_ranges[$#final_ranges]->[1] = max($sorted_ranges[$pos]->[1], $final_ranges[$#final_ranges][1]);
    }
    # if not -- it is a "detached", new range. so add it as is
    else {
        push @final_ranges, $sorted_ranges[$pos];
    }
    $pos++;
}

# here we should have a list - @final_ranges - of non-overlapping, maximum width ranges
# and now we simply calculate their sizes

my $count = 0;
$count += $_->[1] - $_->[0] + 1 for @final_ranges;

say "The result: $count";

# ========================================

__DATA__
3-5
10-14
16-20
12-18

1
5
8
11
17
32