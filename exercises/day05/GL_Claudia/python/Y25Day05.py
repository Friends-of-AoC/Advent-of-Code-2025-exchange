#
# see: https://adventofcode.com/2025/day/5
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
        content = [line.strip() for line in f]

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

baseDir = "exercises/day05/Claudia"
#########################################################################################
# Day 05
#########################################################################################
DAY="05" 

def doPart1(isTest = True):       
    data = loadInput(isTest)
    result = 0

    ROWS = len(data)
    if isTest:
        for y in range(ROWS):
            print(data[y])
        print()

    rangeData=[]
    IDs=[]
    
    for line in data:
        if len(line) == 0:
            continue
        
        if '-' in line:
            rangeData.append((int(line.split('-')[0]), int(line.split('-')[1])))
        else:
            IDs.append(int(line))

    cntRanges = len(rangeData)
    resRanges=[]
    for id in IDs:
        for n in range(cntRanges):
            if id in range(rangeData[n][0], rangeData[n][1]+1):
                result+=1
                if rangeData[n] not in resRanges:
                    resRanges.append(rangeData[n])
                break
    
    if not isTest:
        writeSolutionFile(1, result)

    return result



def doAllParts(part = 1, isTest = True):
    data = loadInput(isTest)
    result = 0
    #if isTest:
    #    for i in range(len(data)):
    #        print(data[i])
    #    print()

    
    rangeData=[]
    IDs=[]
    resRanges=[]
    for line in data:
        if len(line) == 0:
            continue
        
        if '-' in line:
            rangeData.append((int(line.split('-')[0]), int(line.split('-')[1])))
        else:
            IDs.append(int(line))

    for id in IDs:
        for n in range(len(rangeData)):
            if id in range(rangeData[n][0], rangeData[n][1]+1):
                result+=1
                if part == 1:
                    break
                elif rangeData[n] not in resRanges:
                    resRanges.append(rangeData[n])
   
   
    if part == 1:    
        if not isTest:
            writeSolutionFile(part, result)

        return result
    
    resRanges=sorted(resRanges)
    
    #if isTest:
    #    print(resRanges)

    result = 0
    maximal = -1

    for (start, end) in resRanges:
        if maximal >= start:
            start = maximal + 1
        if start <= end:
            result += end-start + 1
        maximal = max(maximal, end)

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
