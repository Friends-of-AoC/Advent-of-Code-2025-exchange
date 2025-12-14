#
# see: https://adventofcode.com/2025/day/9
# 
 
from time import time
from itertools import combinations, pairwise

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

baseDir = "exercises/day09/Claudia"
#########################################################################################
# Day 09
#########################################################################################
DAY="09" 

def doPart1(isTest = True):       
    data = loadInput(isTest)

    points=[]
    for d in data:
        d = d.split(",")
        points.append((int(d[0]), int(d[1])))
    points.sort()
    points.sort(key=lambda x: x[1])
    
    result = 0

    if isTest:
        print(points)

    for i1 in range(len(points)-1):
        for i2 in range(i1 + 1, len(points)):
            left   =min(points[i1][1], points[i2][1])
            right  =max(points[i1][1], points[i2][1])
            top    =min(points[i1][0], points[i2][0])
            bottom =max(points[i1][0], points[i2][0])
           
            result = max(result, (right - left + 1) * (bottom - top + 1) )          

    if not isTest:
        writeSolutionFile(1, result)

    return result


def doPart2(isTest = True):
    data = loadInput(isTest)

    red=[]
   
    for d in data:
        d=d.split(",")
        red.append((int(d[0]), int(d[1])))
        
    result = 0

    for (y1, x1), (y2, x2) in combinations(red, 2):
        y1, y2 = sorted((y1, y2))
        x1, x2 = sorted((x1, x2))
        size = (y2 - y1 + 1) * (x2 - x1 + 1)        

        for (y3, x3), (y4, x4) in pairwise(red + [red[0]]):
            y3, y4 = sorted((y3, y4))
            x3, x4 = sorted((x3, x4))
            if all((y1<y4, y3<y2, x1<x4, x3<x2)):
                break
        else: 
            result = max(result, size)

    if not isTest:
        writeSolutionFile(2, result)
        return result
    
    return result


#########################################################################################

print("--- PART 1 ---")
print(f"Solution Example: {doPart1()}")
print(f"Solution Part 1:  {doPart1(False)}")

print("\n==============\n")
print("--- PART 2 ---")
print(f"Solution Example: {doPart2()}")
print(f"Solution Part 2:  {doPart2(False)}")

#########################################################################################
print()
printTimeTaken()


