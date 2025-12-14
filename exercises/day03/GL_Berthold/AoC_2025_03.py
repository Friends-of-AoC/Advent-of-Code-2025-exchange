# -*- coding: utf-8 -*-
"""
Created on Wed Dec  3 06:00:00 2025
@author: XaverX / Berthold Braun
Advent of Code 2025 03
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
    987654321111111
    811111111111119
    234234234234278
    818181911112111
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
    L = [line for line in IREAD]
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


def best_joltage(bank:str) -> int:
    v = 0
    n = 12
    length = len(bank)
    #
    p = 0
    Z = ""
    print(bank)
    for i in range(n-1, -1, -1):
        z = max(s := bank[p:length-i])
        print(f"{p:3} {i:3} {s:12} {z}")

        Z += z
        p += (s.index(z)+1)
    v = int(Z)
    return v
#


def main() -> int:
    tA = TI.time()
    data = ReadInputData()
    # if pdbg: print(*data, sep="\n")
    #
    #
    # A A A A A A A A A A A A A A A A A A A A A A A A A A A A A A
    #
    print("."*60)
    t0 = TI.time()
    value = 0
    joltages = []
    for bank in data:
        a = max(*bank[:-1])
        p = bank.index(a)
        b = max(*bank[p+1:])
        q = bank[p+1:].index(b)
        z = a+b
        v = int(z)
        joltages.append(v)
        # print(a,p, b, q, z)
    value = sum(joltages)

    #
    t1 = TI.time() - t0
    print(f" < A >  {value:10}{' '*30}{TimeFormat(t1)}\n{'.'*60}")
    #
    #
    #
    # B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B
    #
    print("."*60)
    t0 = TI.time()
    value = 0
    joltages = []
    for bank in data:
        v = best_joltage(bank)
        print(bank, v)
        joltages.append(v)
    value = sum(joltages)
    #
    t2 = TI.time() - t0
    print(f" < B >  {value:10}{' '*30}{TimeFormat(t2)}\n{'.'*60}")
    #
    #
    #
    print()
    print("="*60)
    tZ = TI.time() - tA
    print(f"{" "*48}{TimeFormat(tZ)}")
#


if __name__ == '__main__':
    # A: 17095
    # B: 168794698570517
    if pdbg: print("."*60)
    print(fname:=__file__.replace("\\", "/").rsplit("/")[-1].split(".")[0])
    main()
    if pdbg: print("."*60)
###
