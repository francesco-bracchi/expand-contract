(ns exco.migrator.interface
  (:refer-clojure :exclude [get])
  (:require [exco.migrator.core :as core]))

(defmacro with
  [opts & as]
  `(core/with ~opts ~@as))

(defn init
  []
  (core/init))

(defn bind
  [prj env conn]
  (core/bind prj env conn))

(defn unbind
  [p e]
  (core/unbind p e))

(defn get
  [prj env]
  (core/get prj env))
