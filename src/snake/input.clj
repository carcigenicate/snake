(ns snake.input
  (:require [snake.snake :as s]
            [helpers.key-manager :as km]))

(defn handle-input [state key]
  (let [us #(update state :snake %)]
    (case key
      \w (us s/move-up)
      \s (us s/move-down)
      \a (us s/move-left)
      \d (us s/move-right)

      state)))