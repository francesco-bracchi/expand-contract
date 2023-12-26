(ns exco.format.interface
  (:refer-clojure :exclude [print])
  (:require [puget.printer :as puget]
            [expound.alpha :as expound]))

(defn print
  [val]
  (puget/pprint val))

(defn print-color
  [val]
  (puget/cprint val))

(defn print-error
  [err]
  (expound/printer err))
