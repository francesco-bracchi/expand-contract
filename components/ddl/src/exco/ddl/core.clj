(ns exco.ddl.core
  (:require [clojure.spec.alpha :as s]
            [exco.ddl.spec]))

(def ^:dynamic *adapter*)

(defn valid?
  [s]
  (s/valid? :ddl/t s))

(defn validate!
  [s]
  (when-not (valid? s)
    (throw (ex-info "invalid ddl" (s/explain-data :ddl/t s)))))

(defmacro with
  [s & xs]
  `(binding [*adapter* ~s] ~@xs))

(defn call
  [name & as]
  (-> *adapter* (get name) (apply as)))

(defn create-namespace
  [cmd]
  (call :ddl/create-namespace cmd))

(defn create-table
  [cmd]
  (call :ddl/create-table cmd))

(defn create-proxy
  [cmd]
  (call :ddl/create-proxy cmd))
