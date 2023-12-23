(ns exco.project.migrations
  (:refer-clojure :exclude [apply])
  (:require [exco.schema.interface :as schema]
            [exco.patch-apply.interface :as patch-apply]))

(defn apply
  [{schema0 :schema} {:migration/keys [patch] :as migration}]
  (let [{:keys [errors schema]} (patch-apply/apply schema0 patch)]
    (if errors
      (reduced {:errors errors :schema schema0 :migration migration})
      {:schema schema})))

(defn build
  [migrations]
  (reduce apply {:schema schema/empty} migrations))

(defn valid?
  [migrations]
  (let [{:keys [errors]} (build migrations)]
    (not (seq errors))))
