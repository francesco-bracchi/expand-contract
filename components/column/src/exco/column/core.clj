(ns exco.column.core
  (:refer-clojure :exclude [type])
  (:require [clojure.spec.alpha :as s]
            [exco.column.spec]))

(defn valid?
  [descr]
  (s/valid? :column/t descr))

(defn validate!
  [descr]
  (when-not (valid? descr)
    (throw (ex-info "invalid column definition", {:reason (s/explain-data :column/t descr)})))
  descr)

(def default-fields
  #:column{:nullable? true
           :unique? false
           :primary-key? false})

(defn with-defaults
  [args]
  (merge default-fields args))

(defn make
  [args]
  (-> args
      (with-defaults)
      validate!))

;; (defn nullable
;;   [descr]
;;   (assoc descr :column/nullable? true))

;; (defn not-nullable
;;   [descr]
;;   (assoc descr :column/nullable? false))

;; (defn primary-key!
;;   [descr]
;;   (assoc descr :column/primary-key? true))

;; (defn default
;;   [descr default]
;;   (assoc descr :column/default default))

;; (defn references
;;   [descr table col]
;;   (assoc descr :column/reference {:reference/table table :reference/column col}))

;; (defn type
;;   [descr t]
;;   (assoc descr :column/type t))

;; (defn unique!
;;   [descr]
;;   (assoc descr :column/unique? true))

;; (defn check
;;   [descr chk]
;;   (update descr :column/checks conj chk))
