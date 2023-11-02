(ns exco.strategy.expand
  (:require [exco.strategy.state :as state]
            [exco.ddl.interface :as ddl]))

(defn create-table
  [name table]
  (let [temp-name (gensym "exco_table")]
    (ddl/create-table {:namespace state/*main-ns* :table-name temp-name :table table})
    (ddl/create-proxy {:proxy-namespace state/*next-ns* :proxy-name name
                       :table-namespace state/*main-ns* :table-name temp-name
                       :table table})

    (state/bind! name temp-name)))
