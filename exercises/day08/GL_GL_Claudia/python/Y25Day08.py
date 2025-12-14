#
# see: https://adventofcode.com/2025/day/8
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

baseDir = "exercises/day08/Claudia"
#########################################################################################
# Day 08
#########################################################################################
DAY="08" 

#parent = []
#size = []

def getDistance(p1,p2):
    d=0

    for i in range(len(p1)):
        d += (p1[i] - p2[i])**2

    return d


def find(p, parent):
    if p == parent[p]:
        return p
    
    parent[p] = find(parent[p], parent)

    return parent[p]


def join(p1, p2, parent, size, ROWS):
    p1 = find(p1, parent)
    p2 = find(p2, parent)

    if p1 == p2:
        return False
    
    if size[p1] < size[p2]:
        p1, p2 = p2, p1

    parent[p2] = p1
    size[p1] += size[p2]

    return size[p1] == ROWS


def doPart1(isTest = True):       
    data = loadInput(isTest)
    ROWS = len(data)

    points = []
    for line in data:
        x, y, z = [int(i) for i in line.split(',')]
        points.append((x, y, z))

    result = 0

    if isTest:
        for i in range(ROWS):
            print(points[i])
        print()      

    dist=[]
    for p1 in range(ROWS):
        for p2 in range(p1+1, ROWS):
            dist.append((getDistance(points[p1], points[p2]), p1, p2, points[p1], points[p2]))
    dist=sorted(dist)
    
    if isTest:
        t=10
    else:
        t=1000
    
    size = [1 for _ in range(ROWS)]
    parent = [i for i in range(ROWS)]

    for i in range(t):
        _, i1, i2, p1, p2 = dist[i]
        join(i1, i2, parent, size, ROWS)

    sp = [(size[i], find(i,parent)) for i in range(ROWS)]
    sp.sort(reverse=True)
    
    seen = set()
    result = 1
    cnt = 0
    for s, p in sp:
        if p not in seen:
            seen.add(p)
            result *= s
            cnt += 1
            if cnt == 3:
                break

    if not isTest:
        writeSolutionFile(1, result)

    return result



def doAllParts(part = 1, isTest = True):
    
    data = loadInput(isTest)
    ROWS = len(data)

    points = []
    for line in data:
        x,y,z = [int(i) for i in line.split(',')]
        points.append((x, y, z))

    if part == 0:
        result = 0
    else:
        result = 1

    if isTest and part == 1:
        for i in range(ROWS):
            print(points[i])
        print()      

    dist=[]
    for p1 in range(ROWS):
        for p2 in range(p1+1,ROWS):
            dist.append((getDistance(points[p1],points[p2]), p1, p2, points[p1], points[p2]))
    dist=sorted(dist)
    
    if isTest:
        t=10
    else:
        t=1000
    
    size = [1 for _ in range(ROWS)]
    parent = [i for i in range(ROWS)]

    if part == 1: 
        for i in range(t):
            _, i1, i2, p1, p2 = dist[i]
            join(i1, i2, parent, size, ROWS)
       
        sp = [(size[i], find(i,parent)) for i in range(ROWS)]
        sp.sort(reverse=True)
    
        seen = set()
        result = 1
        cnt = 0
        for s, p in sp:
            if p not in seen:
                seen.add(p)
                result *= s
                cnt += 1
                if cnt == 3:
                    break

    else:
        for _, i1,i2,p1,p2 in dist:
            if join(i1, i2, parent, size, ROWS):
                result = p1[0] * p2[0]
    
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
#print("--- PART 2 ---")
print(f"Solution Example: {doAllParts(2)}")
print(f"Solution Part 2:  {doAllParts(2, False)}")

#########################################################################################
print()
printTimeTaken()
