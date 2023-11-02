(ns exco.handler-drop-table.interface
  (:require [exco.handler-drop-table.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
