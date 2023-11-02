(ns exco.patch-apply.hunk.spec
  (:require [clojure.spec.alpha :as s]
            [exco.hunk.interface]
            [exco.table.interface]
            [exco.index.interface]))


(s/def :check/error
  #{:check/table-duplicate
    :check/table-miss
    :check/table-columns-mismatch
    :check/table-index-conflict
    :check/index-duplicate
    :check/index-miss
    :check/index-table-miss
    :check/index-table-conflict
    :check/index-args-mismatch
    :check/column-duplicate
    :check/column-miss
    :check/column-mismatch
    :check/column-index-conflict
    :check/invalid-reference})

(s/def :check/hunk
  :hunk/t)

(s/def :check/t
  (s/keys :req [:check/hunk
                :check/error]))
