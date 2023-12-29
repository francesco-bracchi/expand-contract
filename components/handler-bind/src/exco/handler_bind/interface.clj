(ns exco.handler-bind.interface
  (:require [exco.handler-bind.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
