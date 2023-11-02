(ns exco.check.interface
  (:require [exco.check.core :as core]))

(defn check
  [schema hunk]
  (core/check schema hunk))
