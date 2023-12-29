(ns exco.handler-init-migrator.interface
  (:require [exco.handler-init-migrator.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
