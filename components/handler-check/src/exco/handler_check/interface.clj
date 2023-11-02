(ns exco.handler-check.interface
  (:require [exco.handler-check.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
