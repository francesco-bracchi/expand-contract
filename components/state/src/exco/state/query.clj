(ns exco.state.query
  (:require [honey.sql :as sql]
            [honey.sql.helpers :as h]))

(def ^:dynamic *opts* {:pretty true})

(sql/register-clause!
 :merge-into
 (fn [clause x]
   (let [[sql & params]
         (if (ident? x)
           (sql/format-expr x)
           (sql/format-dsl x))]
     (into [(str (sql/sql-kw clause) " " sql)] params)))
 :insert-into)

(defn merge-into
  ([table]
   {:merge-into table})
  ([desc table]
   (assoc desc :merge-into table)))

(-> (merge-into :database)
    (h/columns :name :description)
    (h/values [{:name "name"}])
    (sql/format *opts*))

(defn databases
  ([] (databases *opts*))
  ([opts]
   (-> (h/from :database)
       (h/select :id :name :description)
       (sql/format opts))))

;; (databases)


(defn pair->val
  [[n {d :db/description}]]
  {:name (name n)
   :description d})

(defn values
  [dbs]
  (->> dbs
       (map pair->val)
       (into [])))

(defn align-databases
  ([vals] (align-databases vals *opts*))
  ([vals opts]
   (-> (merge-into :database)
       (h/columns :id :name :description)
       (h/values (values vals))
       (sql/format opts))))


(def x (hash "pippo"))

(System/gc)

(= x (hash "pippo"))
