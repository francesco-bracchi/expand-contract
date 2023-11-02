(ns exco.index.interface
  (:require [exco.index.core :as core]))


(defn valid?
  [desc]
  (core/valid? desc))

(defn validate!
  [descr]
  (core/validate! descr))

(defn make
  [table as]
  (core/make table as))

(defn add-arguments
  [descr as]
  (core/add-arguments descr as))
