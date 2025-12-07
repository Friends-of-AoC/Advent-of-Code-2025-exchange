# -*- coding: utf-8 -*-
"""
Created on Sun Dec  7 06:00:00 2025
@author: XaverX / Berthold Braun
Advent of Code 2025 06
"""

# import sys
# from datetime import datetime as DT
import time as TI
import itertools as IT
# import more_itertools as MI
# import regex as RX
# import json as JS
# import queue as QU
# import random as RD
import operator as OP
import functools as FT
# from functools import cache
# import matplotlib.pyplot as PLT
# import numpy as NP


INPUT = """\
.......S.......
...............
.......^.......
...............
......^.^......
...............
.....^.^.^.....
...............
....^.^...^....
...............
...^.^...^.^...
...............
..^...^.....^..
...............
.^.^.^.^.^...^.
...............
"""


# selector what to read/handle
# 0 AoC-task, 1 example-data, 2... further/own test-data

this = 0

# debug/logging or draw/plot ### False / True
pdbg = True
# draw = True

#


def ReadInputData() -> tuple:
    IREAD = []
    if this <= 0: # read from AoC-file - personalized
        fn = "./" + fname + ".dat"
        with open(fn, "rt") as f:
            while (L := f.readline()):
                IREAD.append(L.strip())
    else: # read from common example INPUT - inline - see above
       IREAD = [L.strip()
                for L in INPUT.splitlines()
                if not L.startswith("#")
                ]
    #
    # IREAD = [n for n in IREAD]
    P = dict()
    pbeam = IREAD[0].find("S")
    for y, line in enumerate(IREAD):
        L = P.setdefault(y, [].copy())
        for x, s in enumerate(line):
            if s == "^": L.append(x)
        else:
            P[y] = L
    return P, pbeam, IREAD
#


def TimeFormat(td:float()) -> str:
    flag = True
    td = int(td * (1_000_000 if flag else 1_000))
    if flag: td, us = divmod(td, 1000)
    td, ms = divmod(td, 1000)
    td, ss = divmod(td, 60)
    td, mi = divmod(td, 60)
    td, hh = divmod(td, 24)
    td, dd = divmod(td, 30)
    us = f"{us:03}" if us > 0 or flag else " "*3
    ms = f"{ms:03}"
    ss = f"{ss:02}."
    mi = f"{mi:02}:"
    hh = f"{hh:02}:" if hh > 0 or dd > 0 else " "*3
    dd = f"{dd:02} " if dd > 0 else " "*3
    tf = dd+hh+mi+ss+ms+us
    return tf.strip()
#


def main() -> int:
    tA = TI.time()
    posix, pbeam, field = ReadInputData()
    t0 = TI.time() - tA
    if pdbg and this == 1: print(*field, sep="\n")
    if pdbg and this == 1: 
        for k, L in posix.items():
            print(f"{k:3} {L}")
    print(f" < {this} >  {len(field):10}{len(field[-1]):10}{' '*20}{TimeFormat(t0)}\n{'.'*60}")
    if pdbg: print(pbeam)
    #
    # A A A A A A A A A A A A A A A A A A A A A A A A A A A A A A
    #
    print("."*60)
    t0 = TI.time()
    #
    value = 0
    B = [pbeam] # beam positions in each step
    for y, P in posix.items():
        N = [].copy() # new beam positions
        split = False
        for b in B: # over all beams
            if b in P: # against splitting element
                split = True
                value += 1
                if (a:=b-1) not in N: N.append(a) # split to left
                if (a:=b+1) not in N: N.append(a) # split to right
            else:
                if b not in N: N.append(b) # un-splitted
        else:
            B = N.copy()
            if pdbg and split and this == 1: print(f"{y:4} :: ", *B)
    #
    t1 = TI.time() - t0
    print(f" < A >  {value:20}{' '*20}{TimeFormat(t1)}\n{'.'*60}")
    #
    # B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B
    #
    print("."*60)
    t0 = TI.time()
    #
    B = {pbeam:1} # beam positions and multiplicity in each step - start as single
    for y, P in posix.items():
        N = dict() # new beam positions
        for b, m in B.items(): # over all beam time lines
            if b in P: # against splitting element
                # mm + m new multiplicity of beam at position
                mm = N.setdefault(b-1, 0) 
                N[b-1] = mm + m 
                mm = N.setdefault(b+1, 0)
                N[b+1] = mm + m
            else:
                mm = N.setdefault(b, 0)
                N[b] = mm + m
        else:
            B = {b:mm for b, mm in N.items()}
            if pdbg and this == 1: print(f"{y:4} :: ", *B.items())
    value = sum(B.values())
    #
    t2 = TI.time() - t0
    print(f" < B >  {value:20}{' '*20}{TimeFormat(t2)}\n{'.'*60}")
    #
    #
    #
    print()
    print("="*60)
    tZ = TI.time() - tA
    print(f"{" "*48}{TimeFormat(tZ)}")
#


if __name__ == '__main__':
    # A: 1555
    # B: 12895232295789
    if pdbg: print("."*60)
    print(fname:=__file__.replace("\\", "/").rsplit("/")[-1].split(".")[0])
    main()
    if pdbg: print("."*60)
###
