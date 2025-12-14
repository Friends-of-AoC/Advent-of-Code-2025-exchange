# -*- coding: utf-8 -*-
"""
Created on Thu Dec  4 06:00:00 2025
@author: XaverX / Berthold Braun
Advent of Code 2025 04
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
# import matplotlib.pyplot as PLT
# import numpy as NP


INPUT = """\
    ..@@.@@@@.
    @@@.@.@.@@
    @@@@@.@.@@
    @.@@@@..@.
    @@.@@@@.@@
    .@@@@@@@.@
    .@.@.@.@@@
    @.@@@.@@@@
    .@@@@@@@@.
    @.@.@@@.@.
"""


# selector what to read/handle
# 0 AoC-task, 1 example-data, 2... further/own test-data

this = 0

# debug/logging ### False / True
pdbg = True

#


def ReadInputData() -> tuple:
    IREAD = []
    if this <= 0: # read from AoC-file - personalized
        fn = "./" + fname + ".dat"
        with open(fn, "rt") as f:
            while (L := f.readline()):
                if (line:=L.strip()):
                    IREAD.append(line)
    else: # read from common example INPUT - inline - see above
       IREAD = [line
                for L in INPUT.splitlines()
                if not (line:=L.strip()).startswith("#")
                ]
    #
    grid = ["."+line+"." for line in IREAD]
    length = len(grid)
    width = len(grid[0])
    border = "." * width
    grid.insert(0, border)
    grid.append(border)
    return grid, width-2, length
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


def Extractables(grid:list) -> list:
    L = [] # position of removable paper-roles
    for Y, (lineA, lineB, lineC) in enumerate(MI.triplewise(grid), start=1):
        for X in range(1, len(lineB)-1): # get moving region in each line
            partA = lineA[X-1:X+2] # line above
            partB = lineB[X-1:X+2] # line itself
            partC = lineC[X-1:X+2] # line under
            parts = partA + partB + partC
            roles = parts.count("@")
            # fewer 4 is 3, but count also viewed paper-role itself
            if partB[1] == "@" and roles <= 4:
                L.append( (Y,X) )
    return L
#


def RemoveRoles(G:list, Z:list) -> list:
    for y, g in enumerate(G): # strings are not mutable, working with lists
        g = list(g)
        G[y] = g
    #
    for (v,u) in Z: # remove paper-role at position
        G[v][u] = "."
    #
    for y, g in enumerate(G): # back to strings
        g = "".join(g)
        G[y] = g
    #
    return G
#


def main() -> int:
    tA = TI.time()
    grid, width, length = ReadInputData()
    if pdbg: print(*grid, sep="\n")
    if pdbg: print(width, length)
    #
    #
    # A A A A A A A A A A A A A A A A A A A A A A A A A A A A A A
    #
    print("."*60)
    t0 = TI.time()
    #
    value = len(X := Extractables(grid))
    if pdbg: print(*X)
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
    if pdbg: print(*grid, sep="\n", end="\n\n")
    while len(xtrct := Extractables(grid)) > 0:
        value += len(xtrct)
        grid = RemoveRoles(grid, xtrct)
        if pdbg: print(*grid, sep="\n", end="\n\n")
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
    # A: 1437
    # B: 8765
    if pdbg: print("."*60)
    print(fname:=__file__.replace("\\", "/").rsplit("/")[-1].split(".")[0])
    main()
    if pdbg: print("."*60)
###
