(ns exco.workspace.spec
  (:require [clojure.spec.alpha :as s]
            [exco.migrator.interface.spec]
            [exco.project.interface]))

(s/def ::project-name
  keyword?)

(s/def ::migrator-name
  keyword?)

(s/def :workspace/revision
  (s/and string? (partial re-matches #"[0-9]+\.[0.9]+.[0-9+.](-.+)?")))

(s/def :workspace/projects
  (s/map-of ::project-name :project/t))

(s/def :workspace/default-project
  ::project-name)

(s/def :workspace/migrators
  (s/map-of ::migrator-name :migrator/opts))

(s/def :workspace/default-migrator
  ::migrator-name)

(defn default-project-exists?
  [#:workspace{:keys [projects default-project]}]
  (some? (projects default-project)))

(defn default-migrator-exists?
  [#:workspace{:keys [migrators default-migrator]}]
  (some? (migrators default-migrator)))

(s/def :workspace/t
  (s/and
   (s/keys :req [:workspace/revision
                 :workspace/projects
                 :workspace/default-project
                 :workspace/migrations-dir
                 :workspace/migrators
                 :workspace/default-migrator])
   default-project-exists?
   default-migrator-exists?))
