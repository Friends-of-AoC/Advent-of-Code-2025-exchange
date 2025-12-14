#!/usr/bin/env janet

(def safe-grammar ~{
 :direction (choice
   (/ "L" :left)
   (/ "R" :right))
 :amount (/ (<- (some (range "09"))) ,scan-number)
 :main (sequence :direction :amount) })

# Return array of strings with file contents
(defn get-lines [filename]
  (def lines @[])
  (with [f (file/open filename)]
        (each line (file/lines f)
          (array/push lines (string/trim line))))
  lines)

(defn main [& args]
  (def lines (get-lines "y25day01_input.txt"))
  (def safe-instructions (map |(peg/match safe-grammar $) lines))

  (var dial-position 50) # initial position

  # How many times does the dial point at 0?
  (var count-zero 0)

  (each [dir amount] safe-instructions
    (let [move-amount (if (>= amount 100) (% amount 100) amount)]
      (case dir
        :left
        (do
          (-= dial-position move-amount)
          (when (< dial-position 0)
            (set dial-position (- 100 (math/abs dial-position)))))
        :right
        (do
          (+= dial-position move-amount)
          (when (> dial-position 99)
            (set dial-position (- dial-position 100)))))
      )
    (when (= 0 dial-position)
      (++ count-zero)))

  (print count-zero))
