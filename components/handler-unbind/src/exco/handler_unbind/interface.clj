(ns exco.handler-unbind.interface
  (:require [exco.handler-unbind.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
