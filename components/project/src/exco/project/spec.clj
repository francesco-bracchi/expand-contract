(ns exco.project.spec
  (:require [clojure.spec.alpha :as s]
            [exco.migration.interface]))

(s/def :project/description
  string?)

;;TODO: specize :project/migrations to be able to give better error messages
(s/def :project/migrations
  (s/and
   (s/coll-of :migration/t)
   #_migrations/valid?))

(s/def :project/t
  (s/keys :req [:project/migrations]
          :opt [:project/description]))
