use strict;
use warnings;
use feature 'say';

sub slurp_file {
    return do { local(@ARGV, $/) = shift; <> }
}

sub slurp_data {
    return do { local $/; <DATA> }
}


my $input = @ARGV ? slurp_file($ARGV[0]) : slurp_data();
chomp $input;

my @id_ranges = split ',', $input;
my $invalids_sum = 0;

# go over each range
for my $range (@id_ranges) {
    my ($from, $to) = split '-', $range;

    # go over each ID in the current range
    for my $id ($from .. $to) {
        my $len_full = length($id);
        my $len_half = int($len_full / 2);

        # check for patterns in chunks of size from 1 character, to half the size of the string (max possible)
        for my $chunk_size (1 .. $len_half) {
            if ($len_full % $chunk_size == 0) {
                if ($id =~ m/^(.{$chunk_size})\1+$/) {
                    $invalids_sum += $id;
                    last;
                }
            }
        }
    }
}

say $invalids_sum;

__DATA__

11-22,95-115,998-1012,1188511880-1188511890,222220-222224,1698522-1698528,446443-446449,38593856-38593862,565653-565659,824824821-824824827,2121212118-2121212124
