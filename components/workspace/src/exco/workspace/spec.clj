(ns exco.workspace.spec
  (:require [clojure.spec.alpha :as s]
            [exco.project.interface]))

(s/def :workspace/revision
  (s/and string? (partial re-matches #"[0-9]+\.[0.9]+.[0-9+.](-.+)?")))

(s/def :workspace/projects
  (s/map-of keyword? :project/t))

(s/def :workspace/default-project
  keyword?)

(defn default-db-exists?
  [#:workspace{:keys [projects default-project]}]
  (some? (projects default-project)))

(s/def :workspace/t
  (s/and
   (s/keys :req [:workspace/revision
                 :workspace/projects
                 :workspace/default-project
                 :workspace/migrations-dir])
   default-db-exists?))
