(ns exco.defaults.interface)

(def directory
  ".exco")

(def workspace-file
  "workspace.edn")


(def migrations-dir
  "migrations")

(def project
  :main)

(def migration
  'initialize)

(def color
  true)

(def migrator-type
  :local)

(def migrator-db-url
  "jdbc:sqlite:./.exco/migrator-local.db")

(def env
  "dev")
