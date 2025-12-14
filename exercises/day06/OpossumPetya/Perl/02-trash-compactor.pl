use strict;
use warnings;
use feature 'say';
use builtin 'trim';
use Data::Printer;

sub slurp_file {
    return do { local(@ARGV, $/) = shift; <> }
}

sub slurp_data {
    return do { local $/; <DATA> }
}

my $input = @ARGV ? slurp_file($ARGV[0]) : slurp_data();

my @input_lines = split /\n/, $input;
my @ops = split ' ', pop @input_lines;

# ========================

my $final_total = 0;
my $op = 0;
my @rotated_col_values;
for my $pos (0..length($input_lines[0])) {
    my $new_row = '';
    for my $line (@input_lines) {
        $new_row .= substr $line, $pos, 1;
    }
    # say $new_row;

    if ( trim($new_row)) {
        push @rotated_col_values, trim($new_row);
    } else {
        my $col_total = eval join $ops[$op], @rotated_col_values;
        # say join $ops[$op], @rotated_col_values;
        # say "col_total = $col_total";
        # say '---';
        $final_total += $col_total;
        $op++;
        @rotated_col_values = ();
    }
}

say "The final answer is: $final_total";

__DATA__
123 328  51 64 
 45 64  387 23 
  6 98  215 314
*   +   *   +  