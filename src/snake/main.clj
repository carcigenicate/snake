(ns snake.main
  (:require [quil.core :as q]
            [quil.middleware :as m]

            [helpers.general-helpers :as g]

            [snake.snake :as s])

  (:gen-class))

(defrecord State [snake food])

(def screen-width 1000)
(def screen-height 1000)

(def board-width 100)
(def board-height 100)

(def grid-width (/ screen-width board-width))
(def grid-height (/ screen-height board-height))

(defn board-coord-to-screen [[x y]]
  [(g/map-range x 0 board-width 0 screen-width)
   (g/map-range y 0 board-height 0 screen-height)])

(defn board-coords-to-screen [coords]
  (map board-coord-to-screen
       coords))

(defn draw-body-section [[x y]]
  (q/rect x y board-width board-height))

(defn draw-snake [snake]
  (let [{body :body} snake
        mapped-body (board-coords-to-screen body)]

    (doseq [section mapped-body]
      (draw-body-section section))))

(defn setup-state []
  (let [snake (s/new-snake [(/ board-width 2) (/ board-height 2)])]
    (->State snake [])))

(defn update-state [state]
  state)

(defn draw-state [state]
  (let [{snake :snake} state]
    (draw-snake snake)))


(defn -main []
  (q/defsketch Snake-Game
    :size [screen-width screen-height]

    :setup setup-state
    :update update-state
    :draw draw-state

    :middleware [m/fun-mode]))