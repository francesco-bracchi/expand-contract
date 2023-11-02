(ns exco.ddl-postgresql.core
  (:refer-clojure :exclude [type name remove-ns])
  (:require [clojure.core :as clj]
            [clojure.java.io :as io]
            [selmer.parser :as selmer]
            [exco.ddl.interface]
            [exco.table.interface :as table]))

(def ^:const prefix "ddl-postgresql")

;; TODO: escape \`
(defn name
  [n]
  [:safe (str \` (clj/name n) \`)])

(def type-basics
  {:type/int "bigint"
   :type/boolean "boolean"
   :type/float "real"
   :type/text "text"
   :type/binary "bytea"
   :type/instant "timestamp"
   :type/uuid "uuid"
   :type/json "jsonb"})

(declare type)

(defn type-array
  [[tag child dimensions]]
  (when (= tag :type/array)
    (prn :child child)
    (apply str (type child) (map (constantly "[]") (range dimensions)))))

(defn type-udt
  [[tag val]]
  (when (= tag :type/udt) val))

(def type (some-fn type-basics type-udt type-array))

(selmer/add-filter! :name name)
(selmer/add-filter! :type type)

(def create-namespace-template
  (->> (str prefix \/ "create-namespace.ddl")
       (io/resource)
       (selmer/parse selmer/parse-file)))

(def create-table-template
  (->> (str prefix \/ "create-table.ddl")
       (io/resource)
       (selmer/parse selmer/parse-file)))

(def create-proxy-template
  (->> (str prefix \/ "create-proxy.ddl")
       (io/resource)
       (selmer/parse selmer/parse-file)))

(defn remove-ns
  [m]
  (->> m
       (map (fn [[k v]] [(keyword (clj/name k)) v]))
       (into (empty m))))

(defn from-column
  [col-name col]
  (-> col
      (remove-ns)
      (assoc :name col-name)))

(defn create-table
  [{:keys [namespace table-name] {:table/keys [columns] :as table} :table}]
  {:pre [(table/valid? table)]}
  (selmer/render-template
   create-table-template
   {:namespace namespace
    :table-name table-name
    :columns (vec (map from-column (keys columns) (vals columns)))}))

(defn create-namespace
  [{:keys [namespace]}]
  (selmer/render-template
   create-namespace-template
   {:namespace namespace}))

(defn create-proxy
  [{:keys [proxy-namespace
           proxy-name
           table-namespace
           table-name
           table-descr]}]
  (selmer/render-template
   create-proxy-template
   {:proxy/namespace proxy-namespace
    :proxy/name proxy-name
    :table/namespace table-namespace
    :table/name table-name
    :column/names (->> table-descr (:table/columns) keys vec)}))

(def adapter
   #:ddl{:create-namespace #'create-namespace
         :create-table #'create-table
         :create-proxy #'create-proxy})
