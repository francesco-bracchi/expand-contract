(ns exco.handler.interface
  (:require [exco.handler.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
