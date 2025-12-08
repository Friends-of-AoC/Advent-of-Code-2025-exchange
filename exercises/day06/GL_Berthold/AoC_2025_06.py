# -*- coding: utf-8 -*-
"""
Created on Sat Dec  6 06:00:00 2025
@author: XaverX / Berthold Braun
Advent of Code 2025 06
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
import operator as OP
import functools as FT
# from functools import cache
import matplotlib.pyplot as PLT
# import numpy as NP


INPUT = """\
123 328  51 64 
 45 64  387 23 
  6 98  215 314
*   +   *   +  
"""


# selector what to read/handle
# 0 AoC-task, 1 example-data, 2... further/own test-data

this = 0

# debug/logging or draw/plot ### False / True
pdbg = True
# draw = True

#


def ReadInputData() -> list:
    ### ! need spaces also at end of line for equal length lines without CRLF !
    IREAD = []
    if this <= 0: # read from AoC-file - personalized
        fn = "./" + fname + ".dat"
        with open(fn, "rt") as f:
            while (L := f.readline()):
                IREAD.append(L.replace("\n",""))
    else: # read from common example INPUT - inline - see above
       IREAD = [L.replace("\n","")
                for L in INPUT.splitlines()
                if not L.startswith("#")
                ]
    #
    return IREAD
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


def PrepareA(data:list) -> list:
    O = list(data[-1].split())
    D = [[o] for o in O]
    #
    for n in range(len(data)-1):
        K = list(map(int, data[n].split()))
        for i,k in enumerate(K):
            D[i].append(k)
    #
    return D
#


def PrepareB(data:list) -> list:
    m = len(data[-1]) # length of each line including spaces
    #
    O = list(data[-1].split()) # operators
    D = [[o] for o in O]
    d = len(D)
    #
    d -= 1
    for p in range(m-1, -1, -1): # read char positions from back to front without CRLF
        k = ""
        for n in range(len(data)-1): # get all lines excluding operator
            k += data[n][p]
        else: # we are done with a number/column
            k = k.strip() # blow away possible surrounding spaces
            if k.isdecimal(): # detect empty separator or final columns
                k = int(k) 
                D[d].append(k)
            else:
                d -= 1 # next operator block
    #
    return D
#


def CephaloMath(D:list) -> int: # Octopus Mathematics ;-)
    V = []
    L = len(D)
    runlen = 5
    toggle = True
    for n, d in enumerate(D):
         ops, *Z = d
         if pdbg:
             if (runlen <= n < L-runlen): 
                 if toggle: 
                     print(" ...")
                     toggle = False
             else:
                 print(f"{n:4}   {ops} {Z}")
         match ops: # compute sum or product of all numbers on one block
             case "+":
                 V.append(FT.reduce(OP.add, Z, 0))
             case "*":
                 V.append(FT.reduce(OP.mul, Z, 1))
    return sum(V)
#


def main() -> int:
    tA = TI.time()
    data = ReadInputData()
    t0 = TI.time() - tA
    print(f" < . >  {len(data):10}{len(data[-1]):10}{' '*20}{TimeFormat(t0)}\n{'.'*60}")
    # if pdbg: print(*data, sep="\n")
    #
    #
    # A A A A A A A A A A A A A A A A A A A A A A A A A A A A A A
    #
    print("."*60)
    t0 = TI.time()
    #
    value = 0
    D = PrepareA(data)
    value = CephaloMath(D)
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
    D = PrepareB(data)
    value = CephaloMath(D)
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
    # A: 5784380717354
    # B: 7996218225744
    if pdbg: print("."*60)
    print(fname:=__file__.replace("\\", "/").rsplit("/")[-1].split(".")[0])
    main()
    if pdbg: print("."*60)
###
