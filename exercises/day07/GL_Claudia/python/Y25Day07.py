#
# see: https://adventofcode.com/2025/day/7
# 
 
from time import time


__startTime = time()

def loadInput(isTest = True):
    global __startTime
    
    if isTest:
        filename = f"{baseDir}/input-example.txt"
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

baseDir = "exercises/day07/Claudia"
#########################################################################################
# Day 07
#########################################################################################
DAY="07" 

def doPart1(isTest = True):       
    data = loadInput(isTest)
    ROWS = len(data)
    COLS = len(data[0])
    
    result = 0
    if isTest:
        for x in range(ROWS):
            print(data[x])
        print()      

    beam=set()

    for x in range(COLS):
        if data[0][x] == "S":
            beam.add(x)
            break

    for y in range(ROWS):
        tmpBeam=set()
        for x in range(COLS):
            if x in beam:
                if data[y][x] == "^":
                    tmpBeam.add(x-1)
                    tmpBeam.add(x+1)
                    result+=1
                else:
                    tmpBeam.add(x)
        beam=tmpBeam.copy()

    if not isTest:
        writeSolutionFile(1, result)

    return result



def doAllParts(part = 1, isTest = True):
    
    data = loadInput(isTest)
    ROWS = len(data)
    COLS = len(data[0])

    result = 0
    if isTest and part==1:
        for x in range(ROWS):
            print(data[x])
        print()      

    for x in range(COLS):
        if data[0][x]=="S":
            beam={x:1}
            break

    for y in range(1,ROWS):
        tmpBeam={}
        
        for x in range(COLS):
            if x in beam:
                if data[y][x] == "^":
                    tmpBeam[x-1] = tmpBeam.get(x-1, 0) + beam[x]
                    tmpBeam[x+1] = tmpBeam.get(x+1, 0) + beam[x]

                    if part == 1:
                        result += 1
                else:
                    tmpBeam[x] = tmpBeam.get(x, 0) + beam[x]
        
        beam=tmpBeam.copy()

    if part == 2:
        result = sum(beam[b] for b in beam)
  
    if not isTest:
        writeSolutionFile(part, result)
        return result
    
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
print(f"Solution Part 2:  {doAllParts(2, False)}")

#########################################################################################
print()
printTimeTaken()
