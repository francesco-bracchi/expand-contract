(ns exco.handler-info.core
  (:require [clojure.pprint :as pp]
            [exco.workspace-io.interface :as io]
            [honey.sql :as sql]
            [exco.binding.interface :as binding]
            [exco.connection.interface :as conn]
            [exco.migrator.interface :as migrator]
            [exco.defaults.interface :as defaults]))

(def default-cmd
  {:directory defaults/directory
   :workspace-file defaults/workspace-file})

(defn header
  [& xs]
  (apply println (concat ["-----"] xs ["-----"])))

(defn workspace-info-generic
  [{:workspace/keys [default-migrator
                     default-project
                     migrators
                     projects
                     revision]}]
  (header "general info")
  (println "workspace version:" revision)
  (if (= 1 (count projects))
    (println "project:" (first (keys projects)))
    (do
      (println "available projects:" (vec (keys projects)))
      (println "default project:" default-project)))
  (if (= 1 (count migrators))
    (println "migrator:" (first (keys migrators)))
    (do
      (println "available migrators:" (vec (keys migrators)))
      (println "default migrator:" default-migrator))))

(defn project-row
  [name {:project/keys [description migrations]}]
  {:name name
   :description description
   :migrations (count migrations)})

(defn workspace-info-project
  [name {:project/keys [migrations description]}]
  (header "Project" name)
  (println description)
  (println "migrations:" (count migrations))
  (pp/print-table [:# :file :description]
                  (map #(identity {:# %1
                                   :description (:migration/description %2)
                                   :file (-> %2 meta :file)})
                       (range (count migrations))
                       migrations)))

(defn workspace-info-projects
  [projects]
  (when (> (count projects) 1)
    (header "Projects")
    (pp/print-table (map #(apply project-row %) projects))
    (println))
  (doseq [[name project] projects]
    (workspace-info-project name project)))


(defn remove-namespace-from-keys
  [orig]
  (->> orig
       (map (fn [[k v]] [(keyword (name k)) v]))
       (into {})))

(defn workspace-info-migrator
  [name migrator]
  (header "migrator" name)
  (migrator/with migrator
    ;; todo: build a proxy into migrator driver
    (pp/print-table [:project :env :conn]
                    (map remove-namespace-from-keys
                         (-> (binding/query)
                             (binding/fields)
                             (sql/format)
                             (conn/many!))))))

(defn workspace-info-migrators
  [migrators]
  (when (> (count migrators) 1)
    (header "Migrators")
    (pp/print-table [:name :type]
                    (map (fn [[name {:migrator/keys [type]}]] {:name name :type type}))))
  (doseq [[name migrator] migrators]
    (workspace-info-migrator name migrator)))

(defn info
  [{:workspace/keys [projects migrators] :as ws} as]
  (workspace-info-generic ws)
  (println)
  (workspace-info-projects projects)
  (println)
  (workspace-info-migrators migrators))

(defn handle*
  [{:keys [directory workspace-file] :as cmd}]
  (io/with-base directory
    (info (io/read-file workspace-file) cmd)))

(defn handle
  [cmd]
  (handle* (merge default-cmd cmd)))
