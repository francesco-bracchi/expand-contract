(ns exco.patch-apply.hunk.apply
  (:refer-clojure :exclude [apply])
  (:require [exco.index.interface :as index]
            [exco.table.interface :as table]))

(defmulti apply
  #(:hunk/action %2))

(defmethod apply :default
  [_ hunk]
  (throw (ex-info "unknown hunk" {:hunk hunk})))

(defmethod apply :action/create-table
  [schema #:hunk{:keys [table-name columns]}]
  (update schema :schema/tables assoc table-name (table/make columns)))

(defmethod apply :action/drop-table
  [schema #:hunk{:keys [table-name]}]
  (update schema :schema/tables dissoc table-name))

(defmethod apply :action/drop-table
  [schema #:hunk{:keys [table-name]}]
  (update schema :schema/tables dissoc table-name))

(defmethod apply :action/rename-table
  [schema #:hunk{:keys [name-from name-to]}]
  (-> schema
      (update :schema/tables dissoc name-from)
      (update :schema/tables assoc name-to (-> schema :schema/tables (get name-from)))))

(defmethod apply :action/create-index
  [schema #:hunk{:keys [table-name index-name index-args]}]
  (update schema :schema/indexes assoc index-name (index/make table-name index-args)))

(defmethod apply :action/drop-index
  [schema #:hunk{:keys [index-name]}]
  (update schema :schema/indexes dissoc index-name))

(defmethod apply :action/add-column
  [schema #:hunk{:keys [table-name column-name column]}]
  (update-in schema [:schema/tables table-name :table/columns] assoc column-name column))

(defmethod apply :action/drop-column
  [schema #:hunk{:keys [table-name column-name]}]
  (update-in schema [:schema/tables table-name :table/columns] dissoc column-name))

(defmethod apply :action/rename-column
  [schema #:hunk{:keys [table-name name-from name-to]}]
  (-> schema
      (update-in [:schema/tables table-name :table/columns] dissoc name-from)
      (update-in [:schema/tables table-name :table/columns] assoc name-to (-> schema :schema/tables table-name :table/columns name-from))))

(defmethod apply :action/alter-column
  [schema #:hunk{:keys [table-name column-name column-to]}]
  (update-in schema [:schema/tables table-name :table/columns] assoc column-name column-to))
