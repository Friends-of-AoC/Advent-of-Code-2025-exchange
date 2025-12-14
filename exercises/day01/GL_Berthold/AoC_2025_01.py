# -*- coding: utf-8 -*-
"""
Created on Mon Dec 01 06:00:00 2025
@author: XaverX / Berthold Braun
Advent of Code 2025 01
"""

# import sys
# from datetime import datetime as DT
import time as TI
import itertools as IT
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
"""


# selector what to read/handle
# 0 AoC-task, 1 example-data, 2... further/own test-data
this = 0

# debug/logging ### False / True
pdbg = True

# number of ticks on the dial wheel
DIAL = 100

#


def ReadInputData() -> list:
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
    L = [(line[0], int(line[1:])) for line in IREAD]
    return L
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
    data = ReadInputData()
    if pdbg: print(*data, sep=" ")

    #
    # A A A A A A A A A A A A A A A A A A A A A A A A A A A A A A
    #
    print("."*60)
    t0 = TI.time()
    value = 0
    start = 50
    for dturn, ticks in data:
        if dturn == "L": ticks = DIAL - ticks
        start += ticks
        start %= DIAL
        if start == 0: value += 1
    t1 = TI.time() - t0
    print(f" < A >  {value:10}{' '*30}{TimeFormat(t1)}\n{'.'*60}")
    #

    #
    # B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B
    #
    print("."*60)
    t0 = TI.time()
    value = 0
    start = 50
    for dturn, ticks in data:
        if pdbg: print(f"{start:02}  {dturn:1} {ticks:4} ", end=" : ")
        overt, ticks = divmod(ticks, DIAL) # get multiple turn arounds
        if dturn == "L": # get underflow
            if start > 0 and ticks > start: overt += 1
            ticks = DIAL - ticks
        if dturn == "R": # get overflow
            if start + ticks > DIAL: overt += 1
        if pdbg: print(f"{overt:2} ", end=" : ")
        #
        start += ticks
        start %= DIAL
        if start == 0: value += 1
        value += overt
        if pdbg: print(f" == {start:02}  {value:4}")
    #
    t2 = TI.time() - t0
    print(f" < B >  {value:10}{' '*30}{TimeFormat(t2)}\n{'.'*60}")
    #

    #
    print()
    print("="*60)
    tZ = TI.time() - tA
    print(f"{" "*48}{TimeFormat(tZ)}")
#


if __name__ == '__main__':
    # A: 1086
    # B: 6268
    if pdbg: print("."*60)
    print(fname:=__file__.replace("\\", "/").rsplit("/")[-1].split(".")[0])
    main()
    if pdbg: print("."*60)
###

