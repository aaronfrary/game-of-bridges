(ns bridges.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [bridges.util-test]))

(doo-tests 'bridges.util-test)
