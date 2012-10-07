;;   Copyright (c) Zachary Tellman. All rights reserved.
;;   The use and distribution terms for this software are covered by the
;;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;   which can be found in the file epl-v10.html at the root of this distribution.
;;   By using this software in any fashion, you are agreeing to be bound by
;;   the terms of this license.
;;   You must not remove this notice, or any other, from this software.

(ns pushkin.simulator
  (:require
    [pushkin.hash :as h]
    [pushkin.board :as b]
    [pushkin.position :as p]))

;;;

(defn random-move [board positions color]
  (when-not (empty? positions)
    (let [p (nth (seq positions) (rand-int (count positions)))]
      (if (and
            (not (b/eye? board p))
            (not (b/suicide? board color p))
            (not (b/ko? board color p)))
        p
        (random-move board (disj positions p) color)))))

(defn playout-game [board color pass?]
  (loop [player color, pass? pass?, board board]
    (if-let [move (random-move board (:empty-positions board) player)]
      (recur (p/opponent player) false (b/add-stone board move player))
      (if pass?
        (b/final-score board)
        (recur (p/opponent player) true board)))))

(defn run-playouts [n board color pass?]
  (->> (range n)
    (map (fn [_] (playout-game board color pass?)))
    (map (fn [{:keys [white black]}]
           (cond
             (< white black) 1
             (> white black) -1
             :else 0)))
    (apply +)))


