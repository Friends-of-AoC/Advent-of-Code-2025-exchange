
my $totalscore = 0;

my %map = {};
my @seq = ();

while (<>) {
	chop();
    if ( $_ =~ /^([LR]+)$/ ) {
        @seq = split(//, $1) ;
    } elsif ( $_ =~ /([A-Z]+) = \(([A-Z]+)\, ([A-Z]+)/ ) {
        $map{$1} = { L => $2, R => $3 };
    } 
}

my $mapPos = 'AAA';
my $seqPos = 0;

while ($mapPos ne 'ZZZ') {
print "$mapPos -($seq[$seqPos])-> ";
    $mapPos = $map{$mapPos}{$seq[$seqPos]};
print "$mapPos\n";
    $seqPos++;
    ($seqPos <= $#seq) or ($seqPos = 0);
    $totalscore++;
}



print "\n== $totalscore\n";
