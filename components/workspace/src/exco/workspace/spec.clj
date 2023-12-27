(ns exco.workspace.spec
  (:require [clojure.spec.alpha :as s]
            [exco.project.interface]))

(s/def ::project-name
  keyword?)

(s/def :workspace/revision
  (s/and string? (partial re-matches #"[0-9]+\.[0.9]+.[0-9+.](-.+)?")))

(s/def :workspace/projects
  (s/map-of ::project-name :project/t))

(s/def :workspace/default-project
  ::project-name)

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
