use strict;
use warnings;
use feature 'say';
use builtin 'trim';
use Data::Printer;

my @data_columns;

my $fh;
if (@ARGV) {
    open $fh, '<', $ARGV[0] or die "Can't open file '$ARGV[0]': $!";
} else {
    $fh = *DATA;
}

# process first row to initialize the data array into right number of columns
# we are building a list of arrays, where each array is a column from the data file
@data_columns = map { [trim($_)] }  split ' ', <$fh> ;

while (my $row = <$fh>) {
    chomp $row;                     # drop newline

    my @row_columns = split ' ', $row;
    for my $col (0 ..$#data_columns) {
        push @{$data_columns[$col]}, $row_columns[$col];
    }
}
close $fh;

# ==============================================

my $total = 0;

for my $col (@data_columns) {
    my $op = pop @$col;
    my $expression = join $op, @$col;
    my $total_col_value = eval $expression;
    $total += $total_col_value;
}

say "The grand total is: $total";

# ========================================

__DATA__
123 328  51 64 
 45 64  387 23 
  6 98  215 314
*   +   *   +  