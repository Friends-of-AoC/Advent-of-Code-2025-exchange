#
# see: https://adventofcode.com/2025/day/4
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

baseDir = "exercises/day04/Claudia"
#########################################################################################
# Day 04
#########################################################################################
DAY="04" 


def doPart1(isTest = True):       # Code is only for Part 1 possible
    data = loadInput(isTest)
    result = 0
    if isTest:
        for i in range(len(data)):
            print(data[i])
        print()

    ROWS=len(data)
    COLS=len(data[0])

    for y in range(ROWS):
        for x in range(COLS):
            if data[y][x] == '@':
                adjacentRolls = [data[y+i][x+j] for i in [-1,0,1] for j in [-1,0,1] if not (i==0 and j==0) and 0<=y+i<ROWS and 0<=x+j<COLS and data[y+i][x+j]=='@']
                if len(adjacentRolls) < 4:
                    result += 1
 
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
           
    ROWS=len(data)
    COLS=len(data[0])

    while True:
        resOld = result
        removeRolls = []
        for y in range(ROWS):
            for x in range(COLS):
                if data[y][x] == '@':
                    adjacentRolls = [data[y+i][x+j] for i in [-1,0,1] for j in [-1,0,1] if not (i==0 and j==0) and 0<=y+i<ROWS and 0<=x+j<COLS and data[y+i][x+j]=='@']
                    if len(adjacentRolls) < 4:
                        result += 1
                     
                        if part == 2:
                            removeRolls.append( (y,x) )

        
        if part == 1 or (part == 2 and resOld == result):
            break
        else:
            if isTest:
                print(f"Intermediate Result: {result}") 
            
            for roll in removeRolls:
                data[roll[0]] = data[roll[0]][0:roll[1]] + '.' + data[roll[0]][roll[1]+1:COLS] 
 
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
print(f"Solution Part 2:  {doAllParts(2, False)}")

#########################################################################################
print()
printTimeTaken()
