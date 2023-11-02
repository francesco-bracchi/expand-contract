(ns exco.connection.core
  (:refer-clojure :exclude [seq])
  (:require [next.jdbc :as jdbc]))

(def ^:dynamic *conn*)

(defmacro with
  [ds & xs]
  `(with-open [conn# (jdbc/get-connection (jdbc/get-datasource ~ds))]
     (binding [*conn* conn#]
       ~@xs)))

(defn one!
  ([cmd]
   (jdbc/execute-one! *conn* cmd))
  ([cmd params]
   (jdbc/execute-one! *conn* cmd params)))

(defn many!
  ([cmd]
   (jdbc/execute! *conn* cmd))
  ([cmd params]
   (jdbc/execute! *conn* cmd params)))

(defn seq
  ([cmd]
   (jdbc/plan *conn* cmd))
  ([cmd opts]
   (jdbc/plan *conn* cmd opts)))
