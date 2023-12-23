(ns exco.state.core
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [honey.sql :as sql]
            [honey.sql.helpers :as sqh]
            [jsonista.core :as json]
            [exco.state.query :as q]
            [exco.connection.interface :as conn]))

(def db-spec
  {:jdbcUrl (str "jdbc:h2:mem:state;"
                 "DB_CLOSE_DELAY=0;"
                 "DATABASE_TO_LOWER=TRUE;"
                 "DEFAULT_NULL_ORDERING=HIGH")})

;; todo: build it via migrations
(def init-commands
  (-> "state/create.ddl"
      (io/resource)
      (slurp)
      (string/split #";\n*")
      vec))

(defn init
  ([] (init init-commands))
  ([cs] (doseq [c cs] (conn/one! [c]))))

(conn/with db-spec
  (init))

(defn projects
  []
  (conn/many! (q/projects)))


(defn align-projects
  [dbs]
  (conn/one! (q/align-projects dbs)))

(conn/with db-spec
  (init)
  (conn/one! ["INSERT INTO `project` (name, description) VALUES (?, ?)" "main" "main description"])
  (align-projects {:secondary {:db/migrations [], :db/description "secondary application project"}})
  (projects))

(defn project-id
  [db-name]
  (:project/id
   (conn/one!
    (sql/format {:select [:id]
                 :from [:project]
                 :where [:= :name (name db-name)]}))))

(conn/with db-spec
  (init)
  (conn/one! ["INSERT INTO `project` (name, description) VALUES (?, ?)" "main" "main description"])
  (align-projects {:secondary {:db/migrations [], :db/description "secondary application project"}})
  (project-id :main))

(defn prefix?
  [prefix list]
  (every? identity (map = prefix list)))


(defn migrations-query
  [db]
  (-> (sqh/select :migration.*)
      (sqh/from :project)
      (sqh/join :migration [:= :migration.project_id :project.id])
      (sqh/where [:= :project.name (name db)])))


(defn migrations
  [db]
  (->> db
       (migrations-query)
       (sql/format)
       (conn/many!)
       (map #(update % :migration/patch json/read-value))))

(conn/with db-spec
  (init)
  (conn/one! ["INSERT INTO `project` (name, description) VALUES (?, ?)" "main" "main description"])
  (align-projects {:secondary {:db/migrations [], :db/description "secondary application project"}})
  (conn/one! ["INSERT INTO `migration`(`project_id`, `version`, `patch`) VALUES (?, ?, ? FORMAT JSON)" 1 1 (json/write-value-as-bytes {})])
  (migrations :main))

(defn align-migrations
  [dbname local]
  (let [pre (migrations dbname)
        {db-id :id} (project-id dbname)
        new (drop (count pre) local)]
    (prn new)))

    ;; (-> (sqh/insert-into :migrations)
    ;;     (sqh/columns :project_id :version :description :patch)
    ;;     (sqh/values (map (fn [{:migration/keys [description patch]} version] [db-id version description patch])
    ;;                      new
    ;;                      (range (count pre) (count local))))
    ;;     (conn/one!))))
