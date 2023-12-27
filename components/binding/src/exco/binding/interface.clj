(ns exco.binding.interface
  (:refer-clojure :exclude [get])
  (:require [exco.binding.core :as core]))

(defn ddl
  [d]
  (core/ddl d))

(defn bind!
  [project env conn]
  (core/bind! project env conn))

(def ^:const table
  core/table)

(defn with-project
  [query prj]
  (core/with-project query prj))

(defn with-env
  [query env]
  (core/with-env query env))

(defn get
  [query prj env]
  (core/get query prj env))
