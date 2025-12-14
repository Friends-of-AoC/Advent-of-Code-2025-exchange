use strict;
use warnings;
use feature 'say';

sub slurp_file {
    return do { local(@ARGV, $/) = shift; <> }
}

sub slurp_data {
    return do { local $/; <DATA> }
}

# - invalid ID is made only of some sequence of digits repeated twice
# - none of the numbers have leading zeroes; 0101 isn't an ID at all (not checking for this; no data like this in the provided input file)

my $input = @ARGV ? slurp_file($ARGV[0]) : slurp_data();
chomp $input;

my @id_ranges = split ',', $input;
my $invalids_sum = 0;

for my $range (@id_ranges) {
    my ($from, $to) = split '-', $range;
    for my $id ($from .. $to) {
        my $len = int(length($id) / 2);
        $invalids_sum += $id if $id =~ m/^(.{$len})\1$/;
    }
}

say $invalids_sum;

__DATA__
11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124
