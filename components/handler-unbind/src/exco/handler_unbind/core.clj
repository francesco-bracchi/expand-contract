(ns exco.handler-unbind.core
  (:require [exco.workspace-io.interface :as io]
            [exco.migrator.interface :as migrator]
            [exco.defaults.interface :as defaults]))

(def default-cmd
  {:directory defaults/directory
   :workspace-file defaults/workspace-file
   :env defaults/env})

;;todo: check for the precence of migration_logs so that we can issue a warning
(defn unbind
  [{:workspace/keys [default-project default-migrator migrators]}
   {:keys [migrator project env]}]
  (let [project (or project default-project)
        migrator (or migrator default-migrator)]
    (migrator/with (migrators migrator)
      (migrator/unbind project env))))

(defn handle*
  [{:keys [directory workspace-file] :as cmd}]
  (io/with-base directory
    (unbind (io/read-file workspace-file) cmd)))

(defn handle
  [cmd]
  (handle* (merge default-cmd cmd)))


#_(handle {})
