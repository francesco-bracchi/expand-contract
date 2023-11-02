(ns exco.patch-apply.core
  (:refer-clojure :exclude [apply])
  (:require [clojure.spec.alpha :as s]
            [exco.schema.interface]
            [exco.patch.interface]
            [exco.patch-apply.spec]
            [exco.patch-apply.hunk.spec]
            [exco.patch-apply.hunk.apply :as app]
            [exco.patch-apply.hunk.check :as chk]))

(defn hunk-apply
  [schema hunk]
  (if-let [errors (seq (chk/check schema hunk))]
    {:errors errors}
    {:schema (app/apply schema hunk)}))

(defn apply
  [schema patch]
  (if-not (seq patch)
    {:schema schema}
    (let [{:keys [schema errors]} (hunk-apply schema (first patch))]
      (if (seq errors) {:errors errors}
          (recur schema (rest patch))))))

(defn check
  [schema patch]
  (-> schema (apply patch) :errors))

(s/fdef apply
  :args (s/cat :schema :schema/t :patch :patch/t)
  :ret :patch-apply/either)

(s/fdef check
  :args (s/cat :schema :schema/t :patch :patch/t)
  :ret (s/coll-of :check/t))
