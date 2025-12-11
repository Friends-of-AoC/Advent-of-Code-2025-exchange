#
# see: https://adventofcode.com/2025/day/6
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

baseDir = "exercises/day06/Claudia"
#########################################################################################
# Day 06
#########################################################################################
DAY="06" 

def doPart1(isTest = True):       
    data = loadInput(isTest)
    ROWS = len(data)
    
    for y in range(ROWS):
        data[y] = data[y].split()

    COLS = len(data[0])

    result = 0

    if isTest:
        for y in range(ROWS):
            print(data[y])
        print()      

    for x in range(COLS):
        operands=[]
        erg=0
        for y in range(ROWS-1):
            operands.append(int(data[y][x]))

        erg = 0 if data[-1][x] == "+" else 1
        
        for op in operands:
            if data[-1][x] == "+":
                erg += op
            else:
                erg *= op
        
        result +=erg
    
    if not isTest:
        writeSolutionFile(1, result)

    return result



def doPart2(isTest = True):
    
    data = loadInput(isTest)
    ROWS = len(data)

    result = 0
    if isTest:
        for y in range(ROWS):
            print(data[y])
        print()      

    operands=[]

    # process from right to the left
    x=len(data[0])-1
    while x>-1:
        strNum=""
    
        # search for operands - add operand if operation found in last row
        for y in range(ROWS-1):
            if data[y][x] != " ":
                strNum += data[y][x]
    
        operands.append(int(strNum))
        
        if data[-1][x] != " ": 
            if data[-1][x] == "+":
                erg = 0
            else:
                erg = 1
            
            for op in operands:
                if data[-1][x] == "+":
                    erg += int(op)
                else:
                    erg *= int(op)
    
            result += erg
            
            operands = []
            strNum = ""
            x -= 1
    
        x -= 1
  
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
