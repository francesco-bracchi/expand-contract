(ns exco.patch-apply.interface
  (:refer-clojure :exclude [apply])
  (:require [exco.patch-apply.core :as core]))

(defn check
  [schema patch]
  (core/check schema patch))

(defn apply
  [schema patch]
  (core/apply schema patch))
