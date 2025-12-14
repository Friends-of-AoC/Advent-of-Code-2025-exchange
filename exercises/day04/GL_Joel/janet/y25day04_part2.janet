#!/usr/bin/env janet

# Return array of strings with file contents
(defn get-lines [filename]
  (def lines @[])
  (with [f (file/open filename)]
        (each line (file/lines f)
          (array/push lines (string/trim line))))
  lines)

# ["123" "5678"] -> [ ["1" "2" "3"] ["4" "5" "6"] ]
(defn get-char-grid [lines]
  (map |(map (fn [byte] (string (buffer/from-bytes byte))) $) lines))

(defn get-at [x y grid] # nil if it doesn't exist
  (get (get grid y) x))

(defn adjacent-cells-at [x y grid] [
  (get-at (dec x) (dec y) grid) # top left
  (get-at x (dec y) grid) # top
  (get-at (inc x) (dec y) grid) # top right
  (get-at (dec x) y grid) # left
  (get-at (inc x) y grid) # right
  (get-at (dec x) (inc y) grid) # bottom left
  (get-at x (inc y) grid) # bottom
  (get-at (inc x) (inc y) grid)]) # bottom right

(defn is-roll? [cell] (= "@" cell))
(defn is-roll-at? [x y grid] (is-roll? (get-at x y grid)))

(defn count-adjacent-rolls [x y grid]
  (def adjacent-cells (adjacent-cells-at x y grid))
  (length (filter is-roll? adjacent-cells)))

(defn remove-rolls [grid &opt removed-last removed-total]
  (default removed-last nil)
  (default removed-total 0)

  (if (= 0 removed-last)
    removed-total # return when no more can be removed
    (do
      (var roll-count 0)
      # loop over every cell
      (loop [y :range [0 (length grid)]]
        (def row (y grid))
        (loop [x :range [0 (length row)]]
          (def n-adjacent-rolls (count-adjacent-rolls x y grid)) # how many rolls
          (when (and (is-roll-at? x y grid) (< n-adjacent-rolls 4)) # is it accessible?
            (put row x ".") # remove paper roll
            (++ roll-count))))
      (remove-rolls grid roll-count (+ removed-total roll-count)))))

(defn main [& args]
  (def lines (get-lines "y25day04_input.txt"))
  # (def lines (get-lines "y25day04_demo.txt"))
  (def input-grid (get-char-grid lines))

  (def roll-count (remove-rolls input-grid))

  (pp roll-count))
