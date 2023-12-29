(ns exco.handler-bind.core
  (:require [exco.workspace-io.interface :as io]
            [exco.migrator.interface :as migrator]
            [exco.defaults.interface :as defaults]))

(def default-cmd
  {:directory defaults/directory
   :workspace-file defaults/workspace-file
   :env defaults/env})

(defn bind
  [{:workspace/keys [default-project default-migrator migrators]}
   {:keys [migrator project env db-url]}]
  (let [project (or project default-project)
        migrator (or migrator default-migrator)]
    (migrator/with (migrators migrator)
      (migrator/bind project env db-url))))

(defn handle*
  [{:keys [directory workspace-file] :as cmd}]
  (io/with-base directory
    (bind (io/read-file workspace-file) cmd)))

(defn handle
  [cmd]
  (handle* (merge default-cmd cmd)))
