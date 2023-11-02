(ns exco.handler-help.interface
  (:require [exco.handler-help.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
