
my $totalscore = 0;

my %map = {};
my @pos = ();
my @seq = ();

while (<>) {
	chop();
    if ( $_ =~ /^([LR]+)$/ ) {
        @seq = split(//, $1) ;
    } elsif ( $_ =~ /([A-Z1-9]+[A-Z]) = \(([A-Z1-9]+[A-Z])\, ([A-Z1-9]+[A-Z])/ ) {
        my $p = $1;
        $map{$1} = { L => $2, R => $3 };
        if ( $p =~ /A$/ ) {
            push(@pos, $p);
        }
    } 
}

my $seqPos = 0;

my $mapPos = '';
while ($mapPos ne $pos[0]) {
    length($mapPos) or $mapPos = $pos[0];
    $mapPos = $map{$mapPos}{$seq[$seqPos]};
    $seqPos++;
    ($seqPos <= $#seq) or ($seqPos = 0);
    $totalscore++;
    if ($mapPos =~ /Z$/) {
        print "$totalscore ";
    }
}





print "\n== $totalscore\n";
