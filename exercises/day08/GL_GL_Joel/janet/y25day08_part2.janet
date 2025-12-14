#!/usr/bin/env janet

# Return array of strings with file contents
(defn get-lines [filename]
  (def lines @[])
  (with [f (file/open filename)]
        (each line (file/lines f)
          (array/push lines (string/trim line))))
  lines)

(defn parse-input [lines]
  (defn split-line [line]
    (tuple ;(map scan-number (string/split "," line))))
  (map split-line lines))

# Euclidean distance
#           _________________________________
# d(p,q) = √(p1-q1)^2 + (p2-q2)^2 + (p3-q3)^2
(defn distance [p q]
  (let [[x1 y1 z1] p
        [x2 y2 z2] q]
    (math/sqrt (+
      (math/pow (- x1 x2) 2)
      (math/pow (- y1 y2) 2)
      (math/pow (- z1 z2) 2)))))

(assert (<
  (distance [162 817 812] [425 690 689])
  (distance [162 817 812] [941 993 340])))

(defn index-closest-to [point-index points]
  (var closest nil)
  (var closest-distance nil)
  (def point (point-index points))
  (loop [i :range [0 (length points)] :when (not (= i point-index))]
    (def d (distance (point-index points) (i points)))
    (if (nil? closest)
      (do
        (set closest i)
        (set closest-distance d))
      (when (< d closest-distance)
        (set closest i)
        (set closest-distance d))))
  [closest closest-distance])

(defn closest-to [point-index points]
  (def [p-index dist] (index-closest-to point-index points))
  [(p-index points) dist])

(defn find-closest-pairs [points]
  # Calculate distances
  (def pairs-record @{})
  (each point points
    (each point2 points
      (when
        (and
          (not (= point point2))
          (nil? (get pairs-record [point2 point]))
          (nil? (get pairs-record [point point2])))
        (def dist (distance point point2))
        (put pairs-record [point point2] dist))))
  # Sort results
  (def pairs-array @[])
  (eachp [points dist] pairs-record
    (array/push pairs-array [points dist]))
  (sort-by (fn [[_points dist]] dist) pairs-array))

(defn main [& args]
  (def lines (get-lines "y25day08_input.txt"))
  # (def lines (get-lines "y25day08_demo.txt"))
  (def box-positions (parse-input lines))

  (def circuits @[])

  (defn circuit-contains [box]
    (def [ circuit ] (filter |(has-value? $ box) circuits))
    circuit)

  (defn box-to-str [box]
    (string (0 box) "," (1 box) "," (2 box)))

  (def closest-pairs (find-closest-pairs box-positions))

  (each box box-positions
    (array/push circuits @[box]))

  (each [[box1 box2] _dist] closest-pairs
    (print (string "Connect " (box-to-str box1) " with " (box-to-str box2)))
    (def circuit1 (circuit-contains box1))
    (def circuit2 (circuit-contains box2))
    (var circuits-touch false)
    (def same-circuit
      (and
        (= (length circuit1) (length circuit2))
        (= circuit1 circuit2)))
    (when
      (and
        (not same-circuit)
        (> (length circuit2) 1)
        (> (length circuit1) 1))
      (each box circuit1
        (when (has-value? circuit2 box)
          (set circuits-touch true)
          (break))))
    (cond
      (or circuits-touch # not in the same circuit, connect together
        (not same-circuit))
      (do
        (def new-circuit @[;circuit1 ;circuit2])

        (array/push circuits new-circuit)
        (array/remove circuits (index-of circuit1 circuits))
        (array/remove circuits (index-of circuit2 circuits))

        (when (= 1 (length circuits)) # final join reached
          (def result (* (0 box1) (0 box2)))
          (print "Answer " result)
          (break))))))
