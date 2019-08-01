(ns bridges.util-test
  (:require [cljs.test :refer-macros [deftest testing is]]
            [bridges.util :as util]))

(deftest pairwise-test
  (testing "util/pairwise"
    (is (= (util/pairwise list '(1 2 3 4))
           '((1 2) (1 3) (1 4) (2 3) (2 4) (3 4))))))

(deftest connected-components-test
  (testing "util/connected-components"
    (let [nodes [{:x 1 :y 1 :num 3} {:x 4 :y 1 :num 5}
                 {:x 1 :y 3 :num 4} {:x 4 :y 4 :num 5}]
          e1 {:fst (nth nodes 0) :snd (nth nodes 1)}
          e2 {:fst (nth nodes 0) :snd (nth nodes 2)}
          e3 {:fst (nth nodes 1) :snd (nth nodes 3)}]
      (is (= 4 (count (util/connected-components nodes []))))
      (is (= 3 (count (util/connected-components nodes [e1]))))
      (is (= 2 (count (util/connected-components nodes [e1 e2]))))
      (is (= 1 (count (util/connected-components nodes [e1 e2 e3])))))))
