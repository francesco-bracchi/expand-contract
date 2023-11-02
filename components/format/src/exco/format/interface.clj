(ns exco.format.interface
  (:refer-clojure :exclude [print])
  (:require [puget.printer :as puget]))

(defn print
  [val]
  (puget/pprint val))

(defn print-color
  [val]
  (puget/cprint val))
