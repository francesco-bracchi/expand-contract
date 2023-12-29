(ns exco.cli.core
  (:require [exco.command.interface :as command]
            [exco.handler.interface :as handler]
            [exco.format.interface  :as format])
  (:gen-class))

(defn main
  [& args]
   (-> args
       (command/parse)
       (handler/handle)))

(defn -main
  [& args]
  (try (apply main args)
       (catch clojure.lang.ExceptionInfo ex
         (binding [*out* *err*]
           (format/print-error (:reason (ex-data ex)))
           (println)
           (System/exit 1)))
       (catch Exception ex
         (binding [*out* *err*]
           (println "unknown exception")
           (format/print-color ex)
           (println)
           (System/exit 2)))))

#_(main "init")

#_(main "init-migrator")

#_(main "bind" ":db-url" "\"jdbc:h2:./exco/main-dev.db\"")

#_(main "info")

#_(main "create-table" ":name" "users")

#_(main "add-column" ":table" "users" ":name" "id" ":type" ":type/uuid" ":primary-key" "true")

#_(main "add-column" ":table" "users" ":name" "name" ":type" ":type/text" ":unique" "true")

#_(main "add-column" ":table" "users" ":name" "first_name" ":type" ":type/text")

#_(main "add-column" ":table" "users" ":name" "last_name" ":type" ":type/text")

#_(main "create-migration" ":name" "articles")

#_(main "create-table" ":name" "articles")

#_(main "add-column" ":table" "articles" ":name" "id" ":type" ":type/uuid" ":primary-key" "true")

#_(main "add-column" ":table" "articles" ":name" "title" ":type" ":type/text")

#_(main "add-column" ":table" "articles" ":name" "body" ":type" ":type/text")

#_(main "add-column" ":table" "articles" ":name" "author" ":type" ":type/uuid" ":reference" "[users id]")

#_(main "info")

#_(main "ddl")
