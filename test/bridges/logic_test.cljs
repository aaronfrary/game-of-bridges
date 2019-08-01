(ns bridges.logic-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [bridges.logic :as l]))

(deftest full?-when-full-test
  (testing "logic/full? for a full island"
    (is (l/full?
      [{:fst {:x 0 :y 4 :num 2} :snd {:x 4 :y 4 :num 3} :num 1}
       {:fst {:x 4 :y 0 :num 5} :snd {:x 4 :y 4 :num 3} :num 2}]
      {:x 4 :y 4 :num 3}))))

(deftest full?-when-not-full-test
  (testing "logic/full? for an island that is not full"
    (is (not (l/full?
      [{:fst {:x 0 :y 4 :num 2} :snd {:x 4 :y 4 :num 3} :num 1}
       {:fst {:x 4 :y 0 :num 5} :snd {:x 4 :y 4 :num 3} :num 2}]
      {:x 4 :y 0 :num 5})))))
