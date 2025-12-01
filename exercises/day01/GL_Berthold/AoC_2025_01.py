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
pdbg = False

# number of ticks on the dial wheel
DIAL = 100

#


def ReadInputData() -> list:
    IREAD = []
    if this <= 0:
        fn = "./" + fname + ".txt"
        with open(fn, "rt") as f:
            while (L := f.readline()):
                if (line:=L.strip()):
                    IREAD.append(line)
    else:
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
        print(f"{start:02}  {dturn:1} {ticks:4} ", end=" : ")
        overt, ticks = divmod(ticks, DIAL) # get multiple turn arounds
        if dturn == "L": # get underflow
            if start > 0 and ticks > start: overt += 1
            ticks = DIAL - ticks
        if dturn == "R": # get overflow
            if start + ticks > DIAL: overt += 1
        print(f"{overt:2} ", end=" : ")
        #
        start += ticks
        start %= DIAL
        if start == 0: value += 1
        value += overt
        print(f" == {start:02}  {value:4}")
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


"""
puzzle description

--- Day 1: Secret Entrance ---

The Elves have good news and bad news.

The good news is that they've discovered project management! This has given them the tools
they need to prevent their usual Christmas emergency. For example, they now know
that the North Pole decorations need to be finished soon so that other critical tasks can start on time.

The bad news is that they've realized they have a different emergency:
    according to their resource planning, none of them have any time left to decorate the North Pole!

To save Christmas, the Elves need you to finish decorating the North Pole by December 12th.

Collect stars by solving puzzles. Two puzzles will be made available on each day;
the second puzzle is unlocked when you complete the first. Each puzzle grants one star. Good luck!

You arrive at the secret entrance to the North Pole base ready to start decorating.
Unfortunately, the password seems to have been changed, so you can't get in.
A document taped to the wall helpfully explains:

"Due to new security protocols, the password is locked in the safe below.
Please see the attached document for the new combination."

The safe has a dial with only an arrow on it; around the dial are the numbers 0 through 99 in order.
As you turn the dial, it makes a small click noise as it reaches each number.

The attached document (your puzzle input) contains a sequence of rotations, one per line,
which tell you how to open the safe. A rotation starts with an L or R which indicates
whether the rotation should be to the left (toward lower numbers) or to the right (toward higher numbers).
Then, the rotation has a distance value which indicates how many clicks the dial should be rotated in that direction.

So, if the dial were pointing at 11, a rotation of R8 would cause the dial to point at 19.
After that, a rotation of L19 would cause it to point at 0.

Because the dial is a circle, turning the dial left from 0 one click makes it point at 99.
Similarly, turning the dial right from 99 one click makes it point at 0.

So, if the dial were pointing at 5, a rotation of L10 would cause it to point at 95.
After that, a rotation of R5 could cause it to point at 0.

The dial starts by pointing at 50.

You could follow the instructions, but your recent required official
North Pole secret entrance security training seminar taught you that the safe is actually a decoy.
The actual password is the number of times the dial is left pointing at 0 after any rotation in the sequence.

For example, suppose the attached document contained the following rotations:

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

Following these rotations would cause the dial to move as follows:

    The dial starts by pointing at 50.
    The dial is rotated L68 to point at 82.
    The dial is rotated L30 to point at 52.
    The dial is rotated R48 to point at 0.
    The dial is rotated L5 to point at 95.
    The dial is rotated R60 to point at 55.
    The dial is rotated L55 to point at 0.
    The dial is rotated L1 to point at 99.
    The dial is rotated L99 to point at 0.
    The dial is rotated R14 to point at 14.
    The dial is rotated L82 to point at 32.

Because the dial points at 0 a total of three times during this process,
the password in this example is 3.

Analyze the rotations in your attached document.
What's the actual password to open the door?

--- Part Two ---

You're sure that's the right password, but the door won't open. You knock, but nobody answers.
You build a snowman while you think.

As you're rolling the snowballs for your snowman, you find another security document that must have fallen into the snow:

"Due to newer security protocols, please use password method 0x434C49434B until further notice."

You remember from the training seminar that "method 0x434C49434B" means
you're actually supposed to count the number of times any click causes the dial to point at 0,
regardless of whether it happens during a rotation or at the end of one.

Following the same rotations as in the above example, the dial points at zero a few extra times during its rotations:

    The dial starts by pointing at 50.
    The dial is rotated L68 to point at 82; during this rotation, it points at 0 once.
    The dial is rotated L30 to point at 52.
    The dial is rotated R48 to point at 0.
    The dial is rotated L5 to point at 95.
    The dial is rotated R60 to point at 55; during this rotation, it points at 0 once.
    The dial is rotated L55 to point at 0.
    The dial is rotated L1 to point at 99.
    The dial is rotated L99 to point at 0.
    The dial is rotated R14 to point at 14.
    The dial is rotated L82 to point at 32; during this rotation, it points at 0 once.

In this example, the dial points at 0 three times at the end of a rotation,
plus three more times during a rotation. So, in this example, the new password would be 6.

Be careful: if the dial were pointing at 50, a single rotation like R1000
would cause the dial to point at 0 ten times before returning back to 50!

Using password method 0x434C49434B, what is the password to open the door?

"""
