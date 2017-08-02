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

(def board-width 50)
(def board-height 50)

(def grid-width (/ screen-width board-width))
(def grid-height (/ screen-height board-height))

(def background-color [100 100 100])
(def snake-color [10 255 10])
(def food-color [255 10 10])

(def fps 30) ; TODO: Make this all a function, and check the real frame rate?
(def update-movement-every 0.001) ; seconds
(def update-movement-every-frames (* fps update-movement-every))

(def global-rand-gen (g/new-rand-gen 99))

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

(defn update-movement? []
  (zero? (rem (q/frame-count) update-movement-every-frames)))

(defn update-movement-if-appropriate [state]
  (if (update-movement?)
    (update state :snake s/update-snake-position)
    state))

(defn draw-body-section [[x y]]
  (q/rect x y grid-width grid-height))

(defn draw-snake [snake]
  (let [{body :body} snake
        mapped-body (board-coords-to-screen body)]

    (q/with-fill snake-color
      (doseq [section mapped-body]
        (draw-body-section section)))))

(defn random-snake [n-parts rand-gen]
  (let [body (for [_ (range n-parts)]
               (ph/random-point 0 board-width
                                0 board-height
                                rand-gen))]
    (s/->Snake (vec body) [0 0])))

(defn setup-state []
  (q/frame-rate fps)

  (let [snake (random-snake 100 global-rand-gen)]
    (->State snake [] (km/new-key-manager))))

(defn update-state [state]
  (-> state
      (run-key-presses)
      (update-movement-if-appropriate)))

(defn draw-state [state]
  (apply q/background background-color)

  (let [{snake :snake} state]
    (draw-snake snake)))

(defn key-press-handler
  [state event]
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