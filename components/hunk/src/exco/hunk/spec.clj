(ns exco.hunk.spec
  (:require [clojure.spec.alpha :as s]
            [exco.column.interface]
            [exco.table.interface]
            [exco.index.interface]))

;;TODO: missing rename/alter index
(s/def :hunk/action
  #{:action/create-table
    :action/drop-table
    :action/rename-table
    :action/create-index
    :action/drop-index
    :action/add-column
    :action/drop-column
    :action/rename-column
    :action/alter-column})

(defmulti action
  :hunk/action)

(s/def :hunk/t
  (s/multi-spec action :hunk/action))

(s/def :hunk/table-name
  keyword?)

(s/def :hunk/columns
  (s/map-of keyword? :column/t))

;;; TODO: fixme please
(s/def :hunk/constraints
  (s/map-of keyword? any?))

(s/def :hunk/table
  (s/keys :req [:hunk/action
                :hunk/table-name
                :hunk/columns]
          :opt [:hunk/constraints]))

(defmethod action :action/create-table
  [_]
  :hunk/table)

(defmethod action :action/drop-table
  [_]
  :hunk/table)

(s/def :hunk/name-from keyword?)

(s/def :hunk/name-to keyword?)

(defmethod action :action/rename-table
  [_]
  (s/keys :req [:hunk/action
                :hunk/name-from
                :hunk/name-to]))

(s/def :hunk/index-args
  :index/args)

(s/def :hunk/index
  (s/keys :req [:hunk/action
                :hunk/index-name
                :hunk/table-name
                :hunk/index-args]
          :opt [:hunk/method]))

(defmethod action :action/create-index
  [_]
  :hunk/index)

(defmethod action :action/drop-index
  [_]
  :hunk/index)

(s/def :hunk/column-name
  keyword?)

(s/def :hunk/column
  :column/t)

(s/def :hunk/column-action
  (s/keys :req [:hunk/action
                :hunk/table-name
                :hunk/column-name
                :hunk/column]))

(defmethod action :action/add-column
  [_]
  :hunk/column-action)

(defmethod action :action/drop-column
  [_]
  :hunk/column-action)

(defmethod action :action/rename-column
  [_]
  (s/keys :req [:hunk/action
                :hunk/table-name
                :hunk/name-from
                :hunk/name-to]))

(s/def :hunk/column-name keyword?)

(s/def :hunk/column-from :column/t)

(s/def :hunk/column-to :column/t)

(defmethod action :action/alter-column
  [_]
  (s/keys :req [:hunk/action
                :hunk/table-name
                :hunk/column-name
                :hunk/column-from
                :hunk/column-to]))
