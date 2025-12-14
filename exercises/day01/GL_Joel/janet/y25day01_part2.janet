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

  # How many times does the dial point at 0 but this time at any click in the
  # procedure and not just the end position
  (var count-zero 0)

  (each [dir amount] safe-instructions
    (def spins (math/floor (/ amount 100)))
    (def move-amount (if (>= amount 100) (% amount 100) amount))
    (def prev-position dial-position)
    (var points-at-0 false)
    (case dir
      :left
      (do
        (-= dial-position move-amount) # move
        (when (< dial-position 0)
          (set dial-position (- 100 (math/abs dial-position))))
        (when
          (and
            (> dial-position prev-position)
            (not (= 0 prev-position))) # did it go over 0?
          (print "went below 0")
          (set points-at-0 true)))
      :right
      (do
        (+= dial-position move-amount)
        (when (> dial-position 99)
          (set dial-position (- dial-position 100)))
        (when
          (and
            (< dial-position prev-position)
            (not (= 0 prev-position)))
          (print "went above 99")
          (set points-at-0 true))))

    (when (= 0 dial-position) # landed at 0 exactly
      (set points-at-0 true))

    (when points-at-0
      (++ count-zero))

    (+= count-zero spins) # Add extra spins to the dial

    (print (buffer dir " " amount ": " prev-position " -> " dial-position " (" count-zero ")")))

  (print count-zero))
