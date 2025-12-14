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
  (if (< group-length 1)
    found
    (do
      (when (= 0 (% (length num-str) group-length))
        # when it's divisible by group-length, split the number
        (loop [i :range [0 (length num-str) group-length]]
          (array/push acc (string/slice num-str i (+ i group-length))))
        (set found true)
        # every split number must be equal
        (loop [i :range [0 (dec (length acc))]]
          (when (not (= (i acc) ((inc i) acc)))
            (set found false)
            (break))))
      (if found
        found
        # if not found continue searching for smaller splits
        (has-repeated-consecutive-digits? num-str (dec group-length))))))

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
        (print (string " found: " num-str))
        (+= sum-result (scan-number num-str)))))

  (print (buffer "Answer: " sum-result)))
