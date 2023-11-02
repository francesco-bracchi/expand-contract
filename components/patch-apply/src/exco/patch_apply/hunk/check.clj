(ns exco.patch-apply.hunk.check
  (:require [clojure.spec.alpha :as s]
            [exco.patch-apply.hunk.spec]
            [exco.schema.interface]
            [exco.hunk.interface]))

(defn indexed-by?
  [table-name #:index{:keys [table]}]
  (= table table-name))

(declare of-args?)

(defn of-expression?
  [col ex]
  (or (= col ex)
      (and (coll? ex) (of-args? col (rest ex)))))

(defn of-args?
  [col args]
  (and (seq args)
       (or (of-expression? col (first args))
           (recur col (rest args)))))

(defn arg-of?
  [col {args :index/args}]
  (->> args (map :arg/expression) (of-args? col)))

(defmulti check
  #(:hunk/action %2))

(s/fdef check
  :args (s/cat :s :schema/t :h :hunk/t)
  :ret (s/coll-of :check/t))

(defmethod check :default
  [_ hunk]
  (throw (ex-info "unknown hunk" {:hunk hunk})))

(defn checks
  [& cs]
  (->> cs (remove nil?)))

(defmethod check :action/create-table
  [{:schema/keys [tables]} {:hunk/keys [table-name _columns] :as hunk}]
  (checks (when (tables table-name)
            #:check{:error :check/table-duplicate
                    :hunk hunk})))

(defmethod check :action/drop-table
  [#:schema{:keys [indexes tables]} {:hunk/keys [table-name columns] :as hunk}]
  (checks (when (not (tables table-name))
            #:check{:error :check/table-miss
                    :hunk hunk})
          (when (not= columns (:colums (tables table-name)))
            #:check{:error :check/table-columns-mismatch
                    :hunk hunk})
          (when (some (partial indexed-by? table-name) (vals indexes))
            #:check{:error :check/table-index-conflict
                    :hunk hunk})))

(defmethod check :action/rename-table
  [#:schema{:keys [tables]} {:hunk/keys [name-from name-to] :as hunk}]
  (checks (when-not (tables name-from)
            #:check{:error :check/table-miss
                    :hunk hunk})
          (when (tables name-to)
            #:check{:error :check/table-duplicate
                    :hunk hunk})))

(defmethod check :action/create-index
  [#:schema{:keys [tables indexes]} {:hunk/keys [table-name index-name] :as hunk}]
  (checks (when (indexes index-name)
            #:check{:error :check/index-duplicate
                    :hunk hunk})
          (when (not (tables table-name))
            #:check{:error :check/table-miss
                    :hunk hunk})))

(defmethod check :action/drop-index
  [#:schema{:keys [tables indexes]} {:hunk/keys [table-name index-name args] :as hunk}]
  (checks (when (not (indexes index-name))
            #:check{:error :check/index-miss
                    :hunk hunk})
          (when (tables table-name)
            #:check{:error :check/index-table-conflict
                    :hunk :hunk})
          (when (not= args (:args (indexes index-name)))
            #:check{:error :check/index-args-mismatch
                    :hunk hunk})))

(defmethod check :action/add-column
  [#:schema{:keys [tables]} {:hunk/keys [table-name column-name column] :as hunk}]
  (checks (when (not (tables table-name))
            #:check{:error :check/table-miss
                    :hunk hunk})
          (when (-> tables table-name :table/columns (get column-name))
            #:check{:error :check/column-duplicate
                    :hunk hunk})
          (when-let [{:reference/keys [table column]} (:column/reference column)]
            (when-not (-> tables (get table) :table/columns (get column))
              #:check{:error :check/invalid-reference
                      :hunk :hunk}))))

(defmethod check :action/drop-column
  [#:schema{:keys [indexes tables]} {:hunk/keys [table-name column-name column] :as hunk}]
  (checks (when (not (tables table-name))
            #:check{:error :check/table-miss
                    :hunk hunk})
          (when (not (-> tables table-name :table/columns (get column-name)))
            #:check{:error :check/column-miss
                    :hunk hunk})
          (when (not= (-> tables table-name :table/columns (get column-name)) column)
            #:check{:error :check/column-mismatch
                    :hunk hunk})
          (when (some (partial arg-of? column-name) (vals indexes))
            #:check{:error :check/column-index-conflict
                    :hunk hunk})))

(defmethod check :action/rename-column
  [#:schema{:keys [indexes tables]} {:hunk/keys [table-name name-from name-to] :as hunk}]
  (checks (when (not (tables table-name))
            #:check{:error :check/table-miss
                    :hunk hunk})
          (when (not (-> tables table-name :table/columns (get name-from)))
            #:check{:error :check/column-miss
                    :hunk hunk})
          (when (-> tables table-name :table/columns (get name-to))
            #:check{:error :check/column-duplicate
                    :hunk hunk})
          (when (some (partial arg-of? name-from) (vals indexes))
            #:check{:error :check/column-index-conflict
                    :hunk hunk})))

(defmethod check :action/alter-column
  [#:schema{:keys [tables]} {:hunk/keys [table-name column-name column-from] :as hunk}]
  (checks (when (not (tables table-name))
            #:check{:error :check/table-miss
                    :hunk hunk})
          (when (not (-> tables table-name :table/columns (get column-name)))
            #:check{:error :check/column-miss
                    :hunk hunk})
          (when (not= (-> tables table-name :table/columns (get column-name)) column-from)
            #:check{:error :check/column-mismatch
                    :hunk hunk})))
