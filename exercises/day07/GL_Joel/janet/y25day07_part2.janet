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

  (def start-position
    [0 (index-of "S" (0 tachyon-manifold-diagram))])

  (defn place-num [y x num]
    (put (get tachyon-manifold-diagram y) x num))

  (defn is-num? [thing] (= :number (type thing)))
  (defn num-at? [y x] (is-num? (get-at y x tachyon-manifold-diagram)))

  (place-num ;start-position 1)
  (loop [y :range [0 (dec (length tachyon-manifold-diagram))]]
    (def row (get tachyon-manifold-diagram y))
    (eachp [x cell] row
      (when (is-num? cell)
        (def next [(inc y) x])
        (cond
          (is-splitter? ;next)
          (do # split
            (def left  [(inc y) (dec x)])
            (def right [(inc y) (inc x)])
            (each pos [left right]
              (when (in-diagram? ;pos)
                (if (num-at? ;pos)
                  (place-num ;pos (+ cell (get-at ;pos tachyon-manifold-diagram)))
                  (place-num ;pos cell)))))
          (do # by default, move down the number
            (if (num-at? ;next)
              (place-num ;next (+ cell (get-at ;next tachyon-manifold-diagram)))
              (place-num ;next cell)))))))

  (each row tachyon-manifold-diagram
    (each cell row
      (prin cell))
    (print ""))

  (var total-timelines (+ ;(filter is-num? (last tachyon-manifold-diagram))))
  (print (string "Answer: " total-timelines)))
