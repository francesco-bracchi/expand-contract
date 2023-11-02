(ns exco.handler-create-migration.interface
  (:require [exco.handler-create-migration.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
