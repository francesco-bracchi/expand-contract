(ns exco.workspace.spec
  (:require [clojure.spec.alpha :as s]
            [exco.db.interface]))

(s/def :workspace/revision
  (s/and string? (partial re-matches #"[0-9]+\.[0.9]+.[0-9+.](-.+)?")))

(s/def :workspace/databases
  (s/map-of keyword? :db/t))

(s/def :workspace/default-db
  keyword?)

(defn default-db-exists?
  [#:workspace{:keys [databases default-db]}]
  (some? (databases default-db)))

(s/def :workspace/t
  (s/and
   (s/keys :req [:workspace/revision
                 :workspace/databases
                 :workspace/default-db
                 :workspace/migrations-dir])
   default-db-exists?))
