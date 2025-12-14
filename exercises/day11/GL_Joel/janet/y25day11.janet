#!/usr/bin/env janet

# Return array of strings with file contents
(defn get-lines [filename]
  (def lines @[])
  (with [f (file/open filename)]
        (each line (file/lines f)
          (array/push lines (string/trim line))))
  lines)

(def input-grammar ~{
  :tag (/ (<- (some (choice (range "az") (range "AZ")))) ,keyword)
  :label (sequence :tag ": ")
  :main (sequence :label (group (some (choice :tag " ")))) })

# test
(assert
  (deep=
    (peg/match input-grammar "aaa: you hhh")
    @[:aaa @[:you :hhh]]))

(defn find-paths [device-table]
  (defn walk [device]
    (if (= :out device)
      1
      (do
        (def outputs (get device-table device))
        (+ ;(map walk outputs)))))
  (walk :you))

(defn main [& args]
  (def lines (get-lines "y25day11_input.txt"))
  # (def lines (get-lines "y25day11_demo.txt"))
  (def device-logs (map |(table ;(peg/match input-grammar $)) lines))
  (def device-table (reduce merge @{} device-logs))

  (pp (find-paths device-table)))
