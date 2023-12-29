(ns exco.handler-info.interface
  (:require [exco.handler-info.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
