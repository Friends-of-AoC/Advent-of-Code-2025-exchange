use strict;
use warnings;
use feature 'say';

my $POSITION = 50;
say "Starting at $POSITION ...";

my %move = (
    'L' => sub {(
        ($POSITION - $_[0]) % 100,
        $POSITION - $_[0] > 0 
            ? 0     # did not cross or reach 0;
            : int(
                # if $POSITION == 0, start at zero
                # if $POSITION is > 0 -- "horisontally flip" the position around 100|0
                #   and do it like we do it for going Right 
                ((!$POSITION ? 0 : 100 - $POSITION) + $_[0]) / 100
            )
    )},
    'R' => sub {(
        ($POSITION + $_[0]) % 100,
        int(($POSITION + $_[0]) / 100)
    )},
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
    
    my ($new_pos, $zero_crossings) = $move{ $direction }->($clicks);
    # say "-> Crossed zerro times: $zero_crossings";
    # say "-> Landed on $new_pos";
    
    $password += $zero_crossings;
    $POSITION = $new_pos;
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