
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

while (grep(/Z$/, @pos) <= $#pos) {
    for (my $p=0 ; $p <= $#pos ; $p++) {
#print "$pos[$p] -($seq[$seqPos])-> ";
    $pos[$p] = $map{$pos[$p]}{$seq[$seqPos]};
#print "$pos[$p] ; ";
    }
#print grep(/Z$/, @pos)."\n";
print ((grep(/Z$/, @pos) > 0) ? scalar grep(/Z$/, @pos) : '.');
    $seqPos++;
    ($seqPos <= $#seq) or ($seqPos = 0);
    $totalscore++;
}



print "\n== $totalscore\n";
