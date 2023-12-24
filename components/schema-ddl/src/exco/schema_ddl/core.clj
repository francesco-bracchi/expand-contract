(ns exco.schema-ddl.core
  (:require [clojure.spec.alpha :as s]
            [exco.schema.interface]
            [exco.table.interface]))

(defn escape
  [s]
  (str "`" (name s) "`"))

(def ddl-type
  {:type/uuid "UUID"
   :type/int "INT"
   :type/text "TEXT"})

(defn ddl-column
  [name {:column/keys [type nullable? unique? primary-key? default reference checks]}]
   (interpose
    " "
    (remove nil?
            (concat
             ["  " (escape name) (ddl-type type)]
             (if nullable? ["NULL"] ["NOT NULL"])
             (when checks ["CHECK(" checks ")"])
             (when default ["DEFAULT" default])
             (when unique? ["UNIQUE"])
             (when primary-key? ["PRIMARY KEY"])
             (when reference ["REFERENCES"
                              (str
                               (escape (:reference/table reference))
                               "("
                               (escape (:reference/column reference))
                               ")")])))))

(defn ddl-columns
  [cols]
  (flatten
   (interpose
    [",\n"]
    (map ddl-column
         (keys cols)
         (vals cols)))))

(defn ddl-table
  [name table]
  (concat
   ["CREATE TABLE " (escape name) " (\n"]
   (ddl-columns (:table/columns table))
   [");\n"]))

(defn ddl-tables
  [tables]
  (flatten
   (interpose
    ["\n"]
    (map ddl-table
         (keys tables)
         (vals tables)))))

(defn ddl-indexes
  [indexes]
  [])
  ;(throw (ex-info "Not implemneted yet" {:indexes indexes})))

(defn ddl-stream
  [{:schema/keys [tables indexes]}]
  (concat (ddl-tables tables)
          (ddl-indexes indexes)))

(defn ddl
  [schema]
  (doseq [token (ddl-stream schema)] (print token))
  (println))

(defn ddl-str
  [schema]
  (with-out-str
    (ddl schema)))

(s/fdef ddl
  :args (s/cat :schema :schema/t))

(s/fdef ddl-str
  :args (s/cat :schema :schema/t)
  :ret string?)
