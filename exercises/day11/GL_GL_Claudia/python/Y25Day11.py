#
# see: https://adventofcode.com/2025/day/11
# 
 
from time import time
from functools import cache

__startTime = time()


def loadInput(isTest = True, part = 1):
    global __startTime
    
    if isTest:
        if part == 1:
            filename = f"{baseDir}/input-example.txt"
        else:
            filename = f"{baseDir}/input-example2.txt"
    else:
        filename = f"{baseDir}/input.txt"

    with open(filename) as f:
        content = [line.replace("\n", "") for line in f]
    
    return content


def writeSolutionFile(part, solution):
    filename = f"{baseDir}/solution-for-input.txt"
    parameter = "w" if part==1 else "a"

    with open(filename, parameter) as f:
        f.write(f"Part {part}: {solution}\n")


def printTimeTaken():
    global __startTime
    __endTime = time()
    print("Time: {:.3f}s".format(__endTime-__startTime))

print()

baseDir = "exercises/day11/Claudia"
#########################################################################################
# Day 11
#########################################################################################
DAY="11" 

devices={}

def getCount1(device):
    if device=="out":
        return 1
    
    cnt = 0
    for p in devices[device]:
        cnt += getCount1(p)
        
    return cnt

@cache
def getCount2(device, fft, dac):
    global devices
    if device=="out":
        if fft and dac:
            return 1
        else:
            return 0
    
    cnt = 0
    for p in devices[device]:
        if device=="dac":
            dac = True
        if device=="fft":
            fft = True
        cnt += getCount2(p, fft, dac)
        
    return cnt


def doPart1(isTest = True):       
    global devices

    data = loadInput(isTest, 1)
    
    #if isTest:
    #    print(data)
    
    devices={}

    for d in data:
        device,outputs = d.split(": ")
        outputs=[o for o in outputs.split(" ")]
        devices[device]=outputs
    
    if isTest:
        for d in devices:
            print(f"{d}: {devices[d]}")
        print()

    result = getCount1("you")

    if not isTest:
        writeSolutionFile(1, result)

    return result



def doAllParts(part = 1, isTest = True):
    global devices

    data = loadInput(isTest, part)
    
    #if isTest and part==1:
    #    print(data)
    
    devices={}

    for d in data:
        device,outputs = d.split(": ")
        outputs=[o for o in outputs.split(" ")]
        devices[device]=outputs
    
    if isTest:
        for d in devices:
            print(f"{d}: {devices[d]}")
        print()

    if part == 1:
        result = getCount1("you")
    else:
        fft = dac = False
        result = getCount2("svr", fft, dac)
    
    if not isTest:
        writeSolutionFile(part, result)

    return result


#########################################################################################

print("--- PART 1 ---")
#print(f"Solution Example: {doPart1()}")
#print(f"Solution Part 1:  {doPart1(False)}")
print(f"Solution Example: {doAllParts()}")
print(f"Solution Part 1:  {doAllParts(1, False)}")


print("\n==============\n")
print("--- PART 2 ---")
print(f"Solution Example: {doAllParts(2)}")
getCount2.cache_clear()
print(f"Solution Part 2:  {doAllParts(2, False)}")

#########################################################################################
print()
printTimeTaken()
