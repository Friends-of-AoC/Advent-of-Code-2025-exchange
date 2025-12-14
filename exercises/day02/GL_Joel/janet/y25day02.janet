#!/usr/bin/env janet

# Return array of strings with file contents
(defn get-lines [filename]
  (def lines @[])
  (with [f (file/open filename)]
        (each line (file/lines f)
          (array/push lines (string/trim line))))
  lines)

(defn parse-product-ranges [str]
  (defn split-range [range-str]
    (def [start end] (string/split "-" range-str))
    [(scan-number start) (scan-number end)])
  (let [ranges (string/split "," str)]
    (map split-range ranges)))

(defn has-repeated-consecutive-digits? [num-str &opt group-length]
  (default group-length (math/floor (/ (length num-str) 2)))
  (def acc @[])
  (var found false)
  (if (or (< group-length 2) (not (= 0 (% (length num-str) group-length))))
    found
    (do
      (def first (string/slice num-str 0 group-length))
      (def second (string/slice num-str group-length (* 2 group-length)))
      (= first second))))

(defn valid-repeat-number? [num-str]
  (if (= 2 (length num-str))
    (= 0 (% (scan-number num-str) 11))
    (has-repeated-consecutive-digits? num-str)))

(defn main [& args]
  (def lines (get-lines "y25day02_input.txt"))
  # (def lines (get-lines "y25day02_demo.txt"))
  (def product-ranges (parse-product-ranges (0 lines)))

  (var sum-result 0)
  (each [start end] product-ranges
    (loop [id :range-to [start end]]
      (def num-str (string id))
      (when (valid-repeat-number? num-str)
        (print (string " Found: " num-str))
        (+= sum-result (scan-number num-str)))))

  (print (buffer "Answer: " sum-result)))
