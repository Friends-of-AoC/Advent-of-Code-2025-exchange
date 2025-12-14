use strict;
use warnings;
use feature 'say';

my $POSITION = 50;
say "Starting at $POSITION ...";

my %move = (
    'L' => sub { ($POSITION - $_[0]) % 100 },
    'R' => sub { ($POSITION + $_[0]) % 100 },
);

my $password = 0;

my $fh;

if (@ARGV) {
    open $fh, '<', $ARGV[0] or die "Can't open file '$ARGV[0]': $!";
} else {
    $fh = *DATA;
}

while (my $row = <$fh>) {
    chomp $row;                     # drop newline
    next unless $row;               # skip empty lines
    next if $row =~/^(#|[^LR])/i;   # skip comments and unknown directions
    
    my ($direction, $clicks) = uc($row) =~ /^(L|R)(\d+)$/;
    
    $POSITION = $move{ $direction }->($clicks);
    say "-> Landed on $POSITION";
    
    $password++ if $POSITION == 0;
}
close $fh;

say "Your password is: $password";



__DATA__

L68
L30
R48
L5
R60
L55
L1
L99
R14
L82