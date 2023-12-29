(ns exco.migrator-local.interface.spec
  (:require [clojure.spec.alpha :as s]))

(s/def :migrator-local/opts
  (s/keys
   :req [:migrator/db-url]))

(s/fdef exco.migrator-local.interface/make
  :args (s/cat :m :migator-local/opts)
  :ret :migrator/t)
