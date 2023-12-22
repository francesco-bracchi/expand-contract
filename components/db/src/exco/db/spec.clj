(ns exco.db.spec
  (:require [clojure.spec.alpha :as s]
            [exco.migration.interface]))

(s/def :db/description
  string?)

;;TODO: specize :db/migrations to be able to give better error messages
(s/def :db/migrations
  (s/and
   (s/coll-of :migration/t)
   #_migrations/valid?))

(s/def :db/t
  (s/keys :req [:db/migrations]
          :opt [:db/description]))
