(ns exco.check.core
  (:require [clojure.spec.alpha :as s]
            [exco.check.spec]))

(defmulti check
  #(:hunk/action %2))

(defmethod check :default
  [_ hunk]
  (throw (ex-info "unknown hunk" {:hunk hunk})))

(defn checks
  [& cs]
  (remove cs identity))

(defn indexed-by?
  [table-name #:index{:keys [table]}]
  (= table table-name))

(defmethod check :action/create-table
  [#:schema{:keys [tables]} {:hunk/keys [table-name _columns] :as hunk}]
  (checks (when (tables table-name)
            #:check{:problem :check/table-duplicate
                    :hunk hunk})))

(defmethod check :action/drop-table
  [#:schema{:keys [indexes tables]} {:hunk/keys [table-name columns] :as hunk}]
  (checks (when (not (tables table-name))
            #:check{:problem :check/table-miss
                    :hunk hunk})
          (when (not= columns (:colums (tables table-name)))
            #:check{:problem :check/table-columns-mismatch
                    :hunk hunk})
          (when (some (partial indexed-by? table-name) indexes)
            #:check{:problem :check/table-index-conflict
                    :hunk hunk})))

(defmethod check :action/rename-table
  [#:schema{:keys [tables]} {:hunk/keys [name-from] :as hunk}]
  (checks (when-not (tables name-from)
            #:check{:problem :check/table-miss
                    :hunk hunk})))

(defmethod check :action/create-index
  [#:schema{:keys [tables indexes]} {:hunk/keys [table-name index-name] :as hunk}]
  (checks (when (indexes index-name)
            #:check{:problem :check/index-duplicate
                    :hunk hunk})
          (when (not (tables table-name))
            #:check{:problem :check/table-miss
                    :hunk hunk})))

(defmethod check :action/drop-index
  [#:schema{:keys [tables indexes]} {:hunk/keys [table-name index-name args] :as hunk}]
  (checks (when (not (indexes index-name))
            #:check{:problem :check/index-miss
                    :hunk hunk})
          (when (tables table-name)
            #:check{:problem :check/index-table-conflict
                    :hunk :hunk})))

(s/fdef check
  :args (s/cat :s :schema/t :h :hunk/t)
  :ret (s/coll-of :check/t))
