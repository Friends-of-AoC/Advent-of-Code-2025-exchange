file_path = "input.txt"
file_lines = None
import numpy as np
from collections import deque, defaultdict


MOVE_DOWN = (0, 1)
with open(file_path, 'r') as file:
    file_lines = file.readlines()

manifold_matrix = list()

for line in file_lines:
    m_row = [c for c in list(line) if c != '\n']
    manifold_matrix.append(m_row)


start_node_pos = [i for i, e in enumerate(manifold_matrix[0]) if e == "S"][0]
start_node_pos = (start_node_pos, 0)
times_visited = defaultdict()
times_visited[str(start_node_pos[0])] = 1

H, W = len(manifold_matrix), len(manifold_matrix[0])
timelines = 0




for y in range(H - 1):
    # we will process one row at a time
    # for each new row, we keep track of how many times each column was reached
    n_times_wisited = defaultdict(int)
    y_bellow = y + 1

    for xs, tv in times_visited.items():
        x = int(xs)
        if x < 0 or x >= W:
            timelines += tv # timeline exited if it went over bounds
            continue

        cell_bellow = manifold_matrix[y_bellow][x]

        if cell_bellow == "^":
            if x-1 < 0 or x+1 >= W:
                timelines += 1 # if split would cause timeline to go over bounds, count it
            else:
                n_times_wisited[x-1] += tv
                n_times_wisited[x+1] += tv
        else:
            n_times_wisited[x] += tv # if character is "." we need to carry the number of times this column
                                     # was visited from previous row.

    times_visited = n_times_wisited

timelines += sum(times_visited.values())

print(timelines)

