(ns snake.snake
  (:require [helpers.point-helpers :as ph]))

(defrecord Snake [body])

(defn new-snake [starting-position]
  (->Snake [starting-position]))

(defn feed-at [snake food-position]
  (update snake :body
          #(conj % food-position)))

; TODO: Overly inefficient? Does it matter if the body is in order?
(defn- move-tail-to [snake head-position]
  (update snake :body
          #(let [rem-body (subvec % 0 (dec (count %)))]
             (vec (cons head-position rem-body)))))

; TODO: Doesn't maintain body order. How to know where the head is?
#_
(defn move-tail-to [snake head-position]
  (update snake :body
          #(assoc % (dec (count %)) head-position)))

(defn- move-by [snake offset-point]
  (let [{body :body} snake
        head (first body)]

    (move-tail-to snake
      (ph/add-pts head offset-point))))

(defn move-up [snake]
  (move-by snake [0 -1]))

(defn move-down [snake]
  (move-by snake [0 1]))

(defn move-left [snake]
  (move-by snake [-1 0]))

(defn move-right [snake]
  (move-by snake [1 0]))