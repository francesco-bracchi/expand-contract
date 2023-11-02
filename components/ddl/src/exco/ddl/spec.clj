(ns exco.ddl.spec
  (:require [clojure.spec.alpha :as s]
            [exco.table.interface]))

(s/def ::name
  (s/or :str string?
        :sym symbol?
        :kwd keyword?))

(s/def :ddl/create-namespace
  (s/fspec :args (s/cat :id ::name)
           :ret string?))


(s/def :ddl/create-table
  (s/fspec :args (s/cat :table-name ::name
                        :table-descr :table/t)
           :ret string?))

(s/def ::namespace
  ::name)

(s/def ::table-coord
  (s/keys :req-un [::namespace ::name]))

(s/def ::table
  :table/t)

(s/def :ddl/create-proxy
  (s/fspec :args (s/cat :proxy ::table-coord
                        :orig (s/merge ::table-coord (s/keys :req-un [::table])))
           :ret string?))

(s/def :ddl/t
  (s/keys :req [:ddl/create-namespace
                :ddl/create-table
                :ddl/create-proxy]))
