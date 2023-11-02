(ns exco.handler-add-column.interface
  (:require [exco.handler-add-column.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
