(ns exco.migration-log.core
  (:require [honey.sql.helpers :as sqh]
            [exco.binding.interface :as b]))


(def ^:const table
  (sqh/from :migration_logs))

#_(honey.sql/format table)

(defn bound-to
  [query prj env]
  (sqh/where query [:in :binding_id (-> b/table (b/get prj env) (sqh/select :id))]))

#_(honey.sql/format (bound-to migration-log "main" "dev"))

(defn sorted
  [query]
  (sqh/order-by query :progressive))

(defn with-states
  [query states]
  (sqh/where query [:in :state states]))

(defn with-state
  [query state]
  (with-states query [state]))

(defn insert
  [& data]
  (-> (sqh/insert-into :migration_logs)
      (sqh/values data)))
