(ns snake.main
  (:require [quil.core :as q]
            [quil.middleware :as m]

            [helpers.general-helpers :as g]
            [helpers.key-manager :as km]

            [snake.snake :as s]
            [snake.input :as i]
            [helpers.point-helpers :as ph])

  (:gen-class))

(defrecord State [snake food key-manager])

(def screen-width 1000)
(def screen-height 1000)

(def board-width 10)
(def board-height 10)

(def grid-width (/ screen-width board-width))
(def grid-height (/ screen-height board-height))

(def background-color [100 100 100])
(def snake-color [10 255 10])
(def food-color [255 10 10])

(defn board-coord-to-screen [[x y]]
  [(g/map-range x 0 board-width 0 screen-width)
   (g/map-range y 0 board-height 0 screen-height)])

(defn board-coords-to-screen [coords]
  (map board-coord-to-screen
       coords))

(defn run-key-presses [state]
  (km/reduce-pressed-keys (:key-manager state)
                          i/handle-input
                          state))


(defn draw-body-section [[x y]]
  (q/rect x y grid-width grid-height))

(defn draw-snake [snake]
  (let [{body :body} snake
        mapped-body (board-coords-to-screen body)]

    (q/with-fill snake-color
      (doseq [section mapped-body]
        (draw-body-section section)))))

(defn setup-state []
  (let [snake (s/new-snake [(/ board-width 2) (/ board-height 2)])]
    (->State snake [] (km/new-key-manager))))

(defn update-state [state]
  ; TODO: Call s/update-movement every n seconds.
  (-> state
      (run-key-presses)))

(defn draw-state [state]
  (apply q/background background-color)

  (let [{snake :snake} state]
    (draw-snake snake)))

(defn key-press-handler [state event]
  (let [key (:raw-key event)]
    (update state :key-manager
            #(km/press-key % key))))

(defn key-release-handler [state]
  (let [key (q/raw-key)]
    (update state :key-manager
            #(km/release-key % key))))

(defn -main []
  (q/defsketch Snake-Game
    :size [screen-width screen-height]

    :setup setup-state
    :update update-state
    :draw draw-state

    :key-pressed key-press-handler
    :key-released key-release-handler

    :middleware [m/fun-mode]))