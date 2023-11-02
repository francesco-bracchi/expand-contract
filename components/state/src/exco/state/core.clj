(ns exco.state.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [honey.sql :as sql]
            [honey.sql.helpers :as sqh]
            [exco.workspace.interface :as ws]
            [exco.connection.interface :as conn]
            [exco.migration.interface :as migration]))

(def db-spec {:dbtype "sqlite"
              :dbname "exco"})

(def db-spec {:jdbcUrl "jdbc:sqlite::memory:"})

;; todo: build it via migrations
(def cmd (slurp (io/resource "state/create.ddl")))

(defn init
  []
  (doseq [c (string/split cmd #";\n*")] (conn/one! [c])))

(defn databases
  []
  (conn/one!
   (sql/format {:select [:id :name :description]
                :from [:databases]})))

(defn name-description
  [[k v]]
  [(name k) (:db/description v)])

;;TODO: align to description changes
(defn align-databases
  [dbs]
  (let [pre (->> (databases) (map :name) set)
        cur (->> dbs
                 (remove (comp pre first))
                 (map name-description))]
    (conn/one!
     (sql/format {:insert-into [:databases]
                  :columns [:name :description]
                  :values (vec cur)}))))

(defn database-id
  [db-name]
  (conn/one!
   (sql/format {:select [:id]
                :from [:databases]
                :where [:= :name (name db-name)]})))

(defn prefix?
  [prefix list]
  (every? identity (map = prefix list)))

(defn migrations
  [db]
  (conn/many! (sql/format {:select '*
                           :from [:databases]
                           :where [:= :name (name db)]})))

(defn align-migrations
  [dbname local]
  (let [pre (migrations dbname)
        {db-id :id} (conn/one! (sql/format {:select [:id] :from [:databases] :where [:= :name dbname]}))
        new (drop (count pre) local)]
    (-> (sqh/insert-into :migrations)
        (sqh/columns :database_id :version :description :patch)
        (sqh/values (map (fn [{:migration/keys [description patch]} version] [db-id version description patch])
                         new
                         (range (count pre) (count local))))
        (conn/one!))))


(conn/with db-spec
  (init)
  (migrations :ciao)
  (aling-migrations))
