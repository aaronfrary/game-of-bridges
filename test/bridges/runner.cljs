(ns bridges.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [bridges.core-test]))

(doo-tests 'bridges.core-test)
