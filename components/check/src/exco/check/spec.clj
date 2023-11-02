(ns exco.check.spec
  (:require [clojure.spec.alpha :as s]
            [exco.hunk.interface]
            [exco.patch.interface]
            [exco.table.interface]
            [exco.index.interface]))

(s/def :check/problem
  #{:check/table-duplicate
    :check/table-miss
    :check/table-columns-mismatch
    :check/table-index-conflict
    :check/index-duplicate
    :check/index-miss
    :check/index-table-miss
    :check/index-table-conflict})

(s/def :check/hunk
  :hunk/t)

(s/def :check/t
  (s/keys :req [:check/hunk
                :check/problem]))
