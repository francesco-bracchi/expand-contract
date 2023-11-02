(ns exco.command.spec
  (:require [clojure.spec.alpha :as s]
            [exco.column.interface]))

(def actions
  #{:init
    :check
    :latest
    :create-migration
    :create-table
    :drop-table
    :add-column
    :drop-column
    :schema
    :ddl
    :help})

(s/def ::action
  actions)

(s/def ::workspace-file
  (s/and string? (partial re-matches #"^.*\.edn$")))

(s/def ::directory
  (s/and string? seq))

(s/def ::name
  (s/or :k keyword? :s string? :x symbol?))

(s/def ::migrations-dir
  ::directory)

(s/def ::default-db
  ::name)

(s/def ::database
  ::name)

(s/def ::description
  string?)

(s/def ::color
  boolean?)

(s/def ::table
  ::name)

(s/def ::type
  :column/type)

(s/def ::nullable
  boolean?)

(s/def ::default
  any?)

(s/def ::unique
  boolean?)

(s/def ::primary-key
  boolean?)

(s/def ::reference
  (s/cat :table ::name
         :column ::name))

(defmulti action
  :action)

(s/def :command/t
  (s/multi-spec action :action))

(defmethod action :init
  [_]
  (s/keys :opt-un [::directory
                   ::workspace-file
                   ::migrations-dir
                   ::default-db]))

(defmethod action :check
  [_]
  (s/keys :opt-un [::directory
                   ::workspace-file
                   ::database]))

(defmethod action :latest
  [_]
  (s/keys :opt-un [::directory
                   ::workspace-file
                   ::database]))

(defmethod action :create-migration
  [_]
  (s/keys :req-un [::name]
          :opt-un [::database
                   ::description
                   ::directory
                   ::workspace-file]))

(defmethod action :latest
  [_]
  (s/keys :opt-un [::database
                   ::directory
                   ::workspace-file]))

(defmethod action :create-table
  [_]
  (s/keys :req-un [::name]
          :opt-un [::database
                   ::directory
                   ::workspace-file]))

(defmethod action :drop-table
  [_]
  (s/keys :req-un [::name]
          :opt-un [::database
                   ::directory
                   ::workspace-file]))

(defmethod action :schema
  [_]
  (s/keys :opt-un [::database
                   ::directory
                   ::workspace-file
                   ::color]))

(defmethod action :help
  [_]
  (s/keys :opt-un [::directory
                   ::workspace-file]))

(defmethod action :add-column
  [_]
  (s/keys :req-un [::table
                   ::name
                   ::type]
          :opt-un [::database
                   ::nullable
                   ::default
                   ::unique
                   ::primary-key
                   ::reference]))

(defmethod action :drop-column
  [_]
  (s/keys :req-un [::table
                   ::name]))

(defmethod action :ddl
  [_]
  (s/keys :opt-un [::database]))