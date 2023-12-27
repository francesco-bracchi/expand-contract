(ns exco.project.spec
  (:require [clojure.spec.alpha :as s]
            [exco.migration.interface]))

(s/def :project/description
  string?)

(s/def :project/migrations
  (s/and
   (s/coll-of :migration/t)))

(s/def :project/t
  (s/keys :req [:project/migrations]
          :opt [:project/description]))
