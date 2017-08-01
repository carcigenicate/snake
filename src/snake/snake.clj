(ns snake.snake
  (:require [helpers.point-helpers :as ph]))

(defrecord Snake [body direction])

(defn new-snake [starting-position]
  (->Snake [starting-position] [0 0]))

(defn feed-at [snake food-position]
  (update snake :body
          #(conj % food-position)))

; TODO: Overly inefficient? Does it matter if the body is in order?
(defn- move-tail-to [snake head-position]
  (update snake :body
          #(let [rem-body (subvec % 0 (dec (count %)))]
             (vec (cons head-position rem-body)))))

(defn- change-direction [snake offset-point]
  (assoc snake :direction offset-point))

(defn update-snake-position [snake]
  (let [{body :body direction :direction} snake
        head (first body)]

    (move-tail-to snake
                  (ph/add-pts head direction))))

(defn move-up [snake]
  (change-direction snake [0 -1]))

(defn move-down [snake]
  (change-direction snake [0 1]))

(defn move-left [snake]
  (change-direction snake [-1 0]))

(defn move-right [snake]
  (change-direction snake [1 0]))