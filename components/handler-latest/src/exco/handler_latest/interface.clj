(ns exco.handler-latest.interface
  (:require [exco.handler-latest.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
