-- Advent of Code 2025 - Day 1: Secret Entrance

local function solve(filename)
    local dial = 50   -- starting position
    local count = 0   -- count how often dial points to 0

    for line in io.lines(filename) do
        local direction = line:sub(1, 1)
        local distance = tonumber(line:sub(2))

        if direction == "L" then
            dial = (dial - distance) % 100
        elseif direction == "R" then
            dial = (dial + distance) % 100
        end

        if dial == 0 then
            count = count + 1
        end
    end

    return count
end

-- Main
local filename = arg[1] or "input.txt"
local result = solve(filename)
print("Password: " .. result)
