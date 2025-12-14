-- Advent of Code 2025 - Day 1: Secret Entrance (Part 2)
-- Count every click that lands on 0, not just end of rotation

local function solve(filename)
    local dial = 50
    local count = 0

    for line in io.lines(filename) do
        local direction = line:sub(1, 1)
        local distance = tonumber(line:sub(2))

        if direction == "L" then
            if dial == 0 then
                count = count + math.floor(distance / 100)
            elseif distance >= dial then
                count = count + math.floor((distance - dial) / 100) + 1
            end
            dial = (dial - distance) % 100
        elseif direction == "R" then
            count = count + math.floor((dial + distance) / 100)
            dial = (dial + distance) % 100
        end
    end

    return count
end

-- Main
local filename = arg[1] or "input.txt"
print("Password: " .. solve(filename))
