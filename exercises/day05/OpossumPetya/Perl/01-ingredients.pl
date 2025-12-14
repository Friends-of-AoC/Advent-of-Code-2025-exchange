use strict;
use warnings;
use feature 'say';
use Data::Printer;

my @ranges;
my @unknownIDs;

my $fh;
if (@ARGV) {
    open $fh, '<', $ARGV[0] or die "Can't open file '$ARGV[0]': $!";
} else {
    $fh = *DATA;
}

while (my $row = <$fh>) {
    chomp $row;                     # drop newline
    next unless $row;               # skip empty lines

    if ($row =~ /\-/) {
        # my @range = split /\-/, $row;
        push @ranges, [ split /\-/, $row ];
    }
    else {
        push @unknownIDs, $row;
    }
}
close $fh;

# ==============================================

my $freshness = 0;

for my $id (@unknownIDs) {
    for my $range (@ranges) {
        if ($range->[0] <= $id <= $range->[1]) {
            $freshness++;
            last;
        }
    }
}

say "Number of fresh veggies: $freshness";

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