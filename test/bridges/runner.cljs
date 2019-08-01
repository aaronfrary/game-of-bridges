(ns bridges.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [bridges.util-test]
              [bridges.logic-test]
              ))

(doo-tests
    'bridges.util-test
    'bridges.logic-test
    )
