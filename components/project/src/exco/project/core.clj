(ns exco.project.core
  (:refer-clojure :exclude [empty])
  (:require [clojure.spec.alpha :as s]
            [exco.project.migrations :as migrations]
            [exco.project.spec]))

(defn empty
  ([] #:project{:migrations []})
  ([descr] #:project{:migrations [] :description descr}))

(defn schema
  [{:project/keys [migrations]}]
  (let [{:keys [schema errors]} (migrations/build migrations)]
    (when-not (seq errors) schema)))

(defn schema!
  [{:project/keys [migrations]}]
  (let [{:keys [schema errors] :as data} (migrations/build migrations)]
    (when (seq errors) (throw (ex-info "invalid migration application" data)))
    schema))

(defn migrations-explain
  [{:project/keys [migrations]}]
  (let [{:keys [errors] :as data} (migrations/build migrations)]
    (when (seq errors) data)))

(defn valid?
  [project]
  (s/valid? :project/t project))

(defn validate!
  [project]
  (when-not (valid? project) (throw (ex-info "invalid project" {:project project :reason (s/explain-data :project/t project)})))
  project)

(s/fdef empty
  :ret :project/t)

(s/fdef schema
  :args (s/cat :project :project/t)
  :ret (s/nilable :schema/t))

(s/fdef schema!
  :args (s/cat :project :project/t)
  :ret :schema/t)

(s/fdef valid?
  :ret boolean)

(s/fdef validate!
  :ret :project/t)
