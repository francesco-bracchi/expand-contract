(ns exco.handler-drop-column.interface
  (:require [exco.handler-drop-column.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
