(ns exco.handler-init-migrator.core
  (:require [exco.workspace-io.interface :as io]
            [exco.migrator.interface :as migrator]
            [exco.defaults.interface :as defaults]))

(def default-cmd
  {:directory defaults/directory
   :workspace-file defaults/workspace-file})

(defn init-migrator
  [{:workspace/keys [default-migrator migrators]}
   {:keys [migrator]}]
  (let [migrator (or migrator default-migrator)]
    (migrator/with (migrators migrator) (migrator/init))))

(defn handle*
  [{:keys [directory workspace-file] :as cmd}]
  (io/with-base directory
    (init-migrator (io/read-file workspace-file) cmd)))

(defn handle
  [cmd]
  (handle* (merge default-cmd cmd)))
