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

(defn count-paths [device-table start target]
  (def visited @{})
  (defn visited? [dev] (get visited dev false))
  (defn counter [device]
    (if (= device target)
      1
      (do
        (put visited device true) # mark as visited
        (var total 0)
        (each output (get device-table device @[])
          (when (not (visited? output))
            (+= total (counter output))))
        (put visited device false) # unmark
        total)))
  (counter start))

(defn topo-sort [device-table]
  (def visited @{})
  (def ordered-nodes @[])
  (defn visited? [dev] (get visited dev false))

  (defn dfs [device]
    (put visited device true)
    (each neighbor (get device-table device @[])
      (when (not (visited? neighbor))
        (dfs neighbor)))
    (array/push ordered-nodes device))

  # (eachp [node] device-table
  #   (dfs node))
  (dfs :svr)
  (reverse ordered-nodes))

(defn count-paths-from [graph start topo]
  (def dp @{})
  (each v topo
    (put dp v 0))
  (put dp start 1)
  (each v topo
    (each w (get graph v @[])
      (put dp w (+ (get dp w) (get dp v))))) # edge v -> w
  dp)

(defn main [& args]
  (def lines (get-lines "y25day11_input.txt"))
  # (def lines (get-lines "y25day11_demo2.txt"))
  (def device-logs (map |(table ;(peg/match input-grammar $)) lines))
  (def device-table (reduce merge @{} device-logs))

  # Faulty outputs always hit :fft first then :dac
  (def topo (topo-sort device-table))
  (def paths-from-svr-to (count-paths-from device-table :svr topo))
  (def paths-from-fft-to (count-paths-from device-table :fft topo))
  (def paths-from-dac-to (count-paths-from device-table :dac topo))

  (prin "Answer: ")
  (pp (*
    (get paths-from-svr-to :fft)
    (get paths-from-fft-to :dac)
    (get paths-from-dac-to :out))))
