import numpy as np

file_path = "safeRotations.txt"
file_lines = None

dial0_counter = 0
current_dial = 50


def rotate(dial, command, amount):
    if command == 'L':
        dial = l_rotate(dial, amount)
    if command == 'R':
        dial = r_rotate(dial, amount)
    return dial


def l_rotate(dial, rotAmount):
    dial -= rotAmount
    dial = dial if dial >= 0 else dial + 100
    return dial


def r_rotate(dial, rotAmount):
    dial += rotAmount
    dial = dial if dial < 100 else dial - 100
    return dial


try:
    with open(file_path, 'r') as file:
        file_lines = file.readlines()

        for line in file_lines:
            direction = line[0]
            rotationAmount = int(line[1:])
            if rotationAmount > 100:
                circles = int(rotationAmount / 100)
                rotationAmount = rotationAmount - (circles * 100)
            print('CurrentDial: ' + str(current_dial) + " | Change is " + line)
            current_dial = rotate(current_dial, direction, rotationAmount)
            if current_dial == 0:
                dial0_counter += 1
            print('Result, dial: ' + str(current_dial))


except Exception as e:
    print(f"An error occurred: {e}")

print("Password is: " + str(dial0_counter))



