(ns exco.binding.interface
  (:refer-clojure :exclude [get])
  (:require [exco.binding.core :as core]))

(defn ddl
  [d]
  (core/ddl d))

(defn insert
  []
  (core/insert))

(defn delete
  []
  (core/delete))

(defn query
  []
  (core/query))

(defn fields
  [q]
  (core/fields q))

(defn with-project
  [query prj]
  (core/with-project query prj))

(defn with-env
  [query env]
  (core/with-env query env))
