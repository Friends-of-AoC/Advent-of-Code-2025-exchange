# -*- coding: utf-8 -*-
"""
Created on Fri Dec  5 06:00:00 2025
@author: XaverX / Berthold Braun
Advent of Code 2025 05
"""

# import sys
# from datetime import datetime as DT
import time as TI
import itertools as IT
import more_itertools as MI
# import regex as RX
# import json as JS
# import queue as QU
# import random as RD
# import operator as OP
# import functools as FT
# from functools import cache
import matplotlib.pyplot as PLT
# import numpy as NP


INPUT = """\
    3-5
    10-14
    16-20
    12-18

    1
    5
    8
    11
    17
    32
"""


# selector what to read/handle
# 0 AoC-task, 1 example-data, 2... further/own test-data

this = 0

# debug/logging or draw/plot ### False / True
pdbg = True
draw = True

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
    R = []
    I = []
    for s in IREAD:
        if not s: continue
        if s.find("-") >= 0:
            R.append(tuple(map(int, s.split("-"))))
        else:
            I.append(int(s))
    return sorted(R), sorted(I)
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


def Overlapping(R:list) -> list:
    # R is already sorted by lower bound !
    Q = [] # reduced non overlapping sorted intervalls from R
    Q.append(R[0])
    """
    3-5
    10-14
    12-18
    16-20

    reduced non overlapping sorted intervalls:
    u-----v     u------v     u-------vu---v

    next to explore:
      a-b
      a----b
      a----------b
      a-----------------------b
            a-b : new intervall !
            a--------b
            a------------b
            a-----------------b

    """
    for a,b in R[1:]:
        pass
        n, (u,v) = (m:=len(Q))-1, Q[-1] # pointing to last added intervall
        lower_bound = (a, n if u <= a <= v else m)
        upper_bound = (b, n if u <= b <= v else m)
        if lower_bound[1] == n and upper_bound[1] == n: continue
        if lower_bound[1] == n and upper_bound[1] == m: Q[-1] = (Q[-1][0], b)
        if lower_bound[1] == m and upper_bound[1] == m: Q.append( (a,b) )
    return Q
#


def main() -> int:
    tA = TI.time()
    Ranges, Ingredients = ReadInputData()
    # if pdbg: print(*Ranges, sep="\n")
    # if pdbg: print(*Ingredients, sep="\t")
    #
    #
    # A A A A A A A A A A A A A A A A A A A A A A A A A A A A A A
    #
    print("."*60)
    t0 = TI.time()
    #
    value = 0
    for ingred in Ingredients:
        if any([(ingred in range(u,v+1)) for (u,v) in Ranges]): value += 1
    #
    t1 = TI.time() - t0
    print(f" < A >  {value:20}{' '*20}{TimeFormat(t1)}\n{'.'*60}")
    #
    #
    #
    # B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B
    #
    print("."*60)
    t0 = TI.time()
    #
    value = 0
    Reduced = Overlapping(Ranges)
    for (u,v) in Reduced:
        value += (v+1 - u)
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
    # A: 674
    # B: 352509891817881
    if pdbg: print("."*60)
    print(fname:=__file__.replace("\\", "/").rsplit("/")[-1].split(".")[0])
    main()
    if pdbg: print("."*60)
###
