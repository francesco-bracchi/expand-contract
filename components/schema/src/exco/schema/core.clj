(ns exco.schema.core
  (:refer-clojure :exclude [empty])
  (:require [clojure.spec.alpha :as s]
            [exco.schema.spec]))

(def empty
  #:schema{:tables {} :indexes {}})

(defn valid?
  [s]
  (s/valid? :schema/t s))

(defn validate!
  [s]
  (when-not (valid? s) (throw (ex-info "invalid schema" {:schema s}))))
