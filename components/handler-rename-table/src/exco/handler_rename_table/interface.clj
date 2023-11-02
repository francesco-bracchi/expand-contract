(ns exco.handler-rename-table.interface
  (:require [exco.handler-rename-table.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
