(ns exco.schema.spec
  (:require [clojure.spec.alpha :as s]
            [exco.table.interface]
            [exco.index.interface]))

(s/def :schema/tables
  (s/map-of keyword? :table/t))

(s/def :schema/indexes
  (s/map-of keyword? :index/t))

(s/def :schema/t
  (s/keys :req [:schema/tables
                :schema/indexes]))
