# -*- coding: utf-8 -*-
"""
Created on Tue Dec  2 06:05:05 2025
@author: XaverX / Berthold Braun
Advent of Code 2025 02
"""

# import sys
# from datetime import datetime as DT
import time as TI
import itertools as IT
import regex as RX
# import json as JS
# import queue as QU
# import random as RD
# import operator as OP
# import functools as FT
# from functools import cache
# import matplotlib.pyplot as PLT
# import numpy as NP


INPUT = """\
    11-22,95-115,998-1012,1188511880-1188511890,222220-222224,
    1698522-1698528,446443-446449,38593856-38593862,565653-565659,
    824824821-824824827,2121212118-2121212124
"""


# selector what to read/handle
# 0 AoC-task, 1 example-data, 2... further/own test-data
this = 0

# debug/logging ### False / True
pdbg = True

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
    L = sorted([tuple(map(int, V.split("-"))) for V in "".join(IREAD).split(",")])
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
    garbage = []
    for lower, upper in data:
        print(lower, upper)
        for z in range(lower, upper+1):
            Z = f"{z}"
            if (length:=len(Z)) % 2 == 1: continue
            length //= 2
            if Z[:length] == Z[length:]: garbage.append(z)
    value = sum(garbage)
    t1 = TI.time() - t0
    print(f" < A >  {value:10}{' '*30}{TimeFormat(t1)}\n{'.'*60}")
    #

    #
    # B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B
    #
    print("."*60)
    t0 = TI.time()
    value = 0
    garbage = []
    for lower, upper in data:
        print("==>", lower, upper)
        for z in range(lower, upper+1):
            Z = f"{z}"
            #
            for length in range(1,6): # max length of number z is 10: so need 1,2,3,4,5
                if (A:=Z[:length]) != Z[length:length+length]: continue
                if len(Z) % length != 0: continue
                c = len(Z) // length
                print(z, length, c, A)
                for i in range(2,c):
                    if A != Z[i*length:(i+1)*length]: break
                else:
                    garbage.append(z)
                    break

    value = sum(garbage)
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
    # A: 52316131093
    # B: 69564213293
    if pdbg: print("."*60)
    print(fname:=__file__.replace("\\", "/").rsplit("/")[-1].split(".")[0])
    main()
    if pdbg: print("."*60)
###
