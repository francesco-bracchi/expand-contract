(ns exco.schema-ddl.interface
  (:require [exco.schema-ddl.core :as core]))

(defn ddl
  [schema]
  (core/ddl schema))

(defn ddl-str
  [schema]
  (core/ddl-str schema))
