(ns exco.handler-schema.interface
  (:require [exco.handler-schema.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
