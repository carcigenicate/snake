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

(def board-width 20)
(def board-height 20)

(def grid-width (/ screen-width board-width))
(def grid-height (/ screen-height board-height))

(def text-color [255 255 255])
(def background-color [100 100 100])
(def snake-color [10 255 10])
(def food-color [255 10 10])

(def text-size 100)
(def text-location [(/ screen-width 2) text-size])

(def fps 30) ; TODO: Make this all a function, and check the real frame rate?
(def update-movement-every 0.01) ; seconds
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

(defn random-board-point [rand-gen]
  (mapv int
        (ph/random-point 0 board-width
                         0 board-height
                         rand-gen)))

(defn spawn-new-food [state rand-gen]
  (assoc state :food (random-board-point rand-gen)))

(defn new-state [rand-gen]
  (let [snake (s/new-snake [(/ board-width 2) (/ board-height 2)])]
    (->State snake
             (random-board-point global-rand-gen)
             (km/new-key-manager))))

(defn update-movement? []
  (zero? (rem (q/frame-count) update-movement-every-frames)))

(defn update-movement-if-appropriate [state]
  (if (update-movement?)
    (update state :snake s/update-snake-position)
    state))

(defn score [state]
  (-> state :snake :body count))

(defn board-point-inbounds? [x y]
  (and (<= 0 x board-width)
       (<= 0 y board-height)))

(defn snake-inbounds? [state]
  (let [{snake :snake} state
        {body :body} snake
        [hx hy] (first body)]

    (board-point-inbounds? hx hy)))

(defn game-over? [state]
  (or (not (snake-inbounds? state))
      (s/internal-collision? (:snake state))))

(defn draw-text-centered-on [text x y]
  (let [n-letters (count text)
        ; Not sure why 1/4 offset adjustment is needed.
        ;  Would have thought 1/2 would be better.
        offset (* n-letters text-size 0.25)]

    (q/text text
            (- x offset) y)))

(defn draw-score [state x y]
  (draw-text-centered-on (str (score state))
                         x y))

(defn draw-body-section [[x y]]
  (q/rect x y grid-width grid-height))

(defn draw-snake [snake]
  (let [{body :body} snake
        mapped-body (board-coords-to-screen body)]

    (q/with-fill snake-color
      (doseq [section mapped-body]
        (draw-body-section section)))))

(defn draw-food [state]
  (let [{food :food} state
        [fx fy] (board-coord-to-screen food)]
    (q/with-fill food-color
      (q/rect fx fy grid-width grid-height))))

(defn handle-game-over [state]
  (when (game-over? state)
    (println "Game Over! Score:" (score state))
    #_(q/exit))

  state)

(defn handle-eating-food [state rand-gen]
  (let [{food :food, snake :snake} state
        {body :body} snake
        head (first body)]

    (if (= food head)
      (-> state
          (update :snake #(s/feed-at % head))
          (spawn-new-food rand-gen))

      state)))

(defn setup-state []
  (q/frame-rate fps)

  (q/text-font (q/create-font "Arial" text-size))

  (new-state global-rand-gen))

(defn update-state [state]
  (-> state
      (handle-game-over)
      (run-key-presses)
      (update-movement-if-appropriate)
      (handle-eating-food global-rand-gen)))

(defn draw-state [state]
  (apply q/background background-color)

  (let [{snake :snake} state]
    (draw-snake snake))

  (draw-food state)

  (apply draw-score state text-location))

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