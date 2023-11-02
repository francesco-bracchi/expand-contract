(ns exco.ddl.interface
  (:require [exco.ddl.core :as core]))

(defn valid?
  [a]
  (core/valid? a))

(defn validate!
  [a]
  (core/validate! a))

(defmacro with
  [s & xs]
  `(core/with ~s ~@xs))

(defn create-namespace
  [cmd]
  (core/create-namespace cmd))

(defn create-table
  [cmd]
  (core/create-table cmd))

(defn create-proxy
  [cmd]
  (core/create-proxy cmd))
