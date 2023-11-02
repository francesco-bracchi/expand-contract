(ns exco.handler-create-table.interface
  (:require [exco.handler-create-table.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
