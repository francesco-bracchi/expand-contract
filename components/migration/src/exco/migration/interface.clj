(ns exco.migration.interface
  (:refer-clojure :exclude [empty])
  (:require [exco.migration.core :as core]))

(defn empty
  ([] (core/empty))
  ([descr] (core/empty descr)))
