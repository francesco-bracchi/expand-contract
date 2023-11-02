(ns exco.handler-init.interface
  (:require [exco.handler-init.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
