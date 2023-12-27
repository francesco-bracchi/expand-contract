(ns exco.migration-log.queries
  (:import [java.security MessageDigest])
  (:require [clojure.string :as string]
            [honey.sql :as sql]
            [honey.sql.helpers :as sqh]))

(defn sha1
  [s]
  (->> s
       (.getBytes)
       (.digest (MessageDigest/getInstance "SHA-1"))
       (BigInteger. 1)))

(defn sha1-str
  [s]
  (string/replace (format "%1$42X" (sha1 s)) " " "0"))

(defn ddl
  []
  (-> (sqh/create-table :migration_logs)
      (sqh/with-columns
        [:id :bigint [:not nil] :primary-key :auto_increment]
        [:binding_id :bigint [:references :bindings :id]] ;; index
        [:progressive :bigint [:not nil]]
        [:hash [:varchar 10] [:not nil]]
        [:created_at :timestamp [:default :current_timestamp]]
        [:uid :text [:not nil]]
        [:state [:enum "inactive" "expanding" "expanded" "contracting" "contracted"] [:not nil] [:default "inactive"]]
        [[:constraint :unique_binding_progressive] :unique [:composite :binding_id "progressive"]])))

;; TODO: this must be imported from binding-schema
(defn binding-of
  [project env]
  (-> (sqh/select :id)
      (sqh/from :bindings)
      (sqh/where := :project project)
      (sqh/where := :env env)))

(defn migration
  [{:keys [project migration env progressive uid]}]
  (-> (sqh/insert-into :migration_logs)
      (sql/values [{:binding_id (binding-of project env)
                    :hash (sha1 (:migration/patch migration))
                    :progressive j
                    :uid (or uid (str project "_" j))}])))


(sql/format (ddl) {:pretty true})

(sql/format (migration :main [{:action :insert}] 0))
