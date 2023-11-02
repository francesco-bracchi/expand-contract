(ns exco.handler-ddl.interface
  (:require [exco.handler-ddl.core :as core]))

(defn handle
  [cmd]
  (core/handle cmd))
