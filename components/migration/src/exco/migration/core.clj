(ns exco.migration.core
  (:refer-clojure :exclude [empty])
  (:require [exco.migration.spec]
            [exco.patch.interface :as patch]))

(defn empty
  ([] #:migration{:patch patch/zero})
  ([descr]  #:migration{:description descr :patch patch/zero}))
