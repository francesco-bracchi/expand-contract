(ns exco.migration.spec
  (:require [clojure.spec.alpha :as s]
            [exco.patch.interface]))


(s/def :migration/patch
  :patch/t)

(s/def :migration/desciption
  string?)

(s/def :migration/t
  (s/keys :req [:migration/patch]
          :opt [:migration/description]))
