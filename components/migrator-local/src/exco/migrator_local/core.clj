(ns exco.migrator-local.core
  (:require [honey.sql :as sql] ;; todo: this goes in some interface as dependency for binding and migrations
            [honey.sql.helpers :as sqh]
            [exco.connection.interface :as conn]
            [exco.migration-log.interface :as migration-log]
            [exco.binding.interface :as binding]))

(defn with-fn
  [db-url thunk]
  (conn/with {:jdbcUrl db-url} (thunk)))

(defn guess-dialect
  []
  nil)

(defn init-fn
  []
  (let [d (guess-dialect)]
    (conn/one! (sql/format (binding/ddl d)))
    (conn/one! (sql/format (migration-log/ddl d)))
    #_(conn/one! (binding/ddl d))
    #_(conn/one! (migration-log/ddl d))))

(defn bind-fn
  [prj env conn]
  (-> (binding/insert)
      (sqh/values [{:project (name prj)
                    :env (name env)
                    :conn (str conn)}])
      (sql/format)
      (conn/one!)))

(defn unbind-fn
  [prj env]
  (-> (binding/delete)
      (binding/with-project (name prj))
      (binding/with-env (name env))
      (sql/format)
      (conn/one!)))

(defn get-fn
  [prj env]
  (-> (binding/query)
      (binding/with-env (name env))
      (binding/with-project (name prj))
      (sql/format)
      (conn/one!)))

(defn make
  [{:migrator/keys [db-url] :as mgr}]
  (merge mgr
         #:migrator{:with-fn (partial with-fn db-url)
                    :init-fn #'init-fn
                    :bind-fn #'bind-fn
                    :unbind-fn #'unbind-fn
                    :get-fn #'get-fn}))
