(ns exco.handler.core
  (:require [clojure.spec.alpha :as s]
            [exco.command.interface]
            [exco.handler-help.interface :as help]
            [exco.handler-init.interface :as init]
            [exco.handler-latest.interface :as latest]
            [exco.handler-schema.interface :as schema]
            [exco.handler-create-migration.interface :as create-migration]
            [exco.handler-create-table.interface :as create-table]
            [exco.handler-drop-table.interface :as drop-table]
            [exco.handler-rename-table.interface :as rename-table]
            [exco.handler-add-column.interface :as add-column]
            [exco.handler-drop-column.interface :as drop-column]
            [exco.handler-ddl.interface :as ddl]
            [exco.handler-check.interface :as check]))

(defmulti handle
  :action)

(defmethod handle :default
  [cmd]
  (throw (ex-info "invalid command" {:command cmd})))

(defmethod handle :init
  [cmd]
  (init/handle cmd))

(defmethod handle :create-migration
  [cmd]
  (create-migration/handle cmd))

(defmethod handle :latest
  [cmd]
  (latest/handle cmd))

(defmethod handle :check
  [cmd]
  (check/handle cmd))

(defmethod handle :create-table
  [cmd]
  (create-table/handle cmd))

(defmethod handle :drop-table
  [cmd]
  (drop-table/handle cmd))

(defmethod handle :rename-table
  [cmd]
  (rename-table/handle cmd))

(defmethod handle :schema
  [cmd]
  (schema/handle cmd))

(defmethod handle :help
  [cmd]
  (help/handle cmd))

(defmethod handle :add-column
  [cmd]
  (add-column/handle cmd))

(defmethod handle :drop-column
  [cmd]
  (drop-column/handle cmd))

(defmethod handle :ddl
  [cmd]
  (ddl/handle cmd))

(s/fdef handle
  :args (s/cat :cmd :command/t))
