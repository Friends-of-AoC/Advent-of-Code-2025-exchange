file_path = "input.txt"
file_lines = None
import numpy as np
import pandas as pd
from collections import deque


MOVE_DOWN = (0, 1)
with open(file_path, 'r') as file:
    file_lines = file.readlines()

manifold_matrix = list()

for line in file_lines:
    m_row = [c for c in list(line) if c != '\n']
    manifold_matrix.append(m_row)


start_node_pos = [i for i, e in enumerate(manifold_matrix[0]) if e == "S"][0]
copied_table = manifold_matrix.copy()
start_node_pos = (start_node_pos, 0)
def is_position_visited(p_pos, visited_positions):
    for vp in visited_positions:
        if vp[0] == p_pos[0] and vp[1] == p_pos[1]:
            return True
    return False


class Node:
    def __init__(self, pos: tuple):
        self.pos = pos
        self.neighbors = list()

    def expand(self, table):
        one_down_pos = (self.pos[0], self.pos[1] + 1)
        if one_down_pos[1] > len(table) - 1 or one_down_pos[0] > len(table[0]) - 1:
            return 0
        if table[self.pos[1]+1][self.pos[0]] == "." or table[self.pos[1]+1][self.pos[0]] == "|":
            self.neighbors.append(Node(one_down_pos))
            copied_table[self.pos[1]+1][self.pos[0]] = "|"
            return 0
        else:
            left_subbeam_pos = (self.pos[0] - 1, self.pos[1] + 1)
            right_subbeam_pos = (self.pos[0] + 1, self.pos[1] + 1)



            copied_table[self.pos[1]+1][self.pos[0] - 1] = "|"
            copied_table[self.pos[1]+1][self.pos[0] + 1] = "|"

            self.neighbors.append(Node(left_subbeam_pos))
            self.neighbors.append(Node(right_subbeam_pos))
            return 1




queue = deque()
visited = set()

start_node = Node(start_node_pos)
queue.append(start_node)
visited.add(start_node.pos)
positions_where_split_happened = 0

while queue:
    current_node = queue.popleft()
    positions_where_split_happened += current_node.expand(manifold_matrix)

    for n in current_node.neighbors:
        if not is_position_visited(n.pos, visited):
            visited.add(n.pos)
            queue.append(n)

print(positions_where_split_happened)

