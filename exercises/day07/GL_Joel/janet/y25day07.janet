#!/usr/bin/env janet

# Return array of strings with file contents
(defn get-lines [filename]
  (def lines @[])
  (with [f (file/open filename)]
        (each line (file/lines f)
          (array/push lines (string/trim line))))
  lines)

(defn get-char-grid [lines]
  (map |(map (fn [byte] (string (buffer/from-bytes byte))) $) lines))

(defn get-at [y x grid] # nil if it doesn't exist
  (get (get grid y) x))

(defn in-range? [y x grid]
  (not (nil? (get-at y x grid))))

(defn main [& args]
  (def lines (get-lines "y25day07_input.txt"))
  # (def lines (get-lines "y25day07_demo.txt"))
  (def tachyon-manifold-diagram (get-char-grid lines))

  (defn is-splitter? [y x]
    (= "^" (get-at y x tachyon-manifold-diagram)))

  (defn in-diagram? [y x]
    (in-range? y x tachyon-manifold-diagram))

  (var total-splits 0)
  (def beam-record @{})

  (def start-position
    [0 (index-of "S" (0 tachyon-manifold-diagram))])

  (defn walked? [y x]
    (not (nil? (get beam-record [y x]))))

  (defn walk [last-pos]
    # Move down
    (def y (inc (0 last-pos)))
    (def x (1 last-pos))
    (print (string y " " x))
    (if (is-splitter? y x)
      (do #split
        (def right [y (inc x)])
        (def left  [y (dec x)])
        (def in-diagram-right (in-diagram? ;right))
        (def in-diagram-left (in-diagram? ;left))
        (when (or in-diagram-right in-diagram-left)
          (put beam-record [y x] :split))
        # keep track of walked beams to not repeat walking the same path
        # exponential times
        (when (and in-diagram-right (not (walked? ;right)))
          (put beam-record right :beam)
          (walk right))
        (when (and in-diagram-left (not (walked? ;left)))
          (put beam-record left :beam)
          (walk left)))
      (when (in-diagram? y x)
        (walk [y x])))) # else continue straight if possible
  (walk start-position)

  (set total-splits (get (frequencies beam-record) :split))
  (print (string "Answer: " total-splits)))
