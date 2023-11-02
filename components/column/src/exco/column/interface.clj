(ns exco.column.interface
  (:refer-clojure :exclude [type])
  (:require [exco.column.core :as c]))


(defn valid?
  [descr]
  (c/valid? descr))

(defn validate!
  [descr]
  (c/validate! descr))

(defn make
  [type]
  (c/make type))

;; (defn nullable
;;   [descr]
;;   (c/nullable descr))

;; (defn not-nullable
;;   [descr]
;;   (c/not-nullable descr))

;; (defn primary-key!
;;   [descr]
;;   (c/primary-key! descr))

;; (defn default
;;   [descr default]
;;   (c/default descr default))

;; (defn references
;;   [descr table col]
;;   (c/references descr table col))

;; (defn type
;;   [descr t]
;;   (c/type descr t))

;; (defn unique!
;;   [descr]
;;   (c/unique! descr))

;; (defn check
;;   [descr chk]
;;   (c/check descr chk))
