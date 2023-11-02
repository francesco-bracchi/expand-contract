(ns exco.db.core
  (:refer-clojure :exclude [empty])
  (:require [clojure.spec.alpha :as s]
            [exco.db.migrations :as migrations]
            [exco.db.spec]))

(defn empty
  ([] #:db{:db/migrations []})
  ([descr] #:db{:db/migrations [] :description descr}))

(defn schema
  [{:db/keys [migrations]}]
  (let [{:keys [schema errors]} (migrations/build migrations)]
    (when-not (seq errors) schema)))

(defn schema!
  [{:db/keys [migrations]}]
  (let [{:keys [schema errors] :as data} (migrations/build migrations)]
    (when (seq errors) (throw (ex-info "invalid migration application" data)))
    schema))

(defn migrations-explain
  [{:db/keys [migrations]}]
  (let [{:keys [errors] :as data} (migrations/build migrations)]
    (when (seq errors) data)))

(defn valid?
  [db]
  (s/valid? :db/t db))

(defn validate!
  [db]
  (when-not (valid? db) (throw (ex-info "invalid db" {:db db :reason (s/explain-data :db/t db)})))
  db)

(s/fdef empty
  :ret :db/t)

(s/fdef schema
  :args (s/cat :db :db/t)
  :ret (s/nilable :schema/t))

(s/fdef schema!
  :args (s/cat :db :db/t)
  :ret :schema/t)

(s/fdef valid?
  :ret boolean)

(s/fdef validate!
  :ret :db/t)
