(ns exco.index.spec
  (:require [clojure.spec.alpha :as s]))

(s/def :index/unique?
  boolean?)

(s/def :index/table
  keyword?)

(s/def :index/method
  (s/and string? seq))

(s/def :arg/sort-order
  #{:sort-order/asc :sort-order/desc})

(s/def :arg/column
  keyword?)

(s/def :arg/application
  (s/cat :fun keyword?
         :args (s/* (s/or :col :arg/column
                          :exp :app/application))))

(s/def :arg/expression
  (s/or :col :arg/column
        :app :arg/application))

(s/def :index/arg
  (s/keys :req [:arg/expression]
          :opt [:arg/sort-order]))

(s/def :index/args
  (s/& (s/coll-of :index/arg) seq))

(s/def :index/t
  (s/keys :req [:index/table
                :index/args]
          :opt [:index/method]))
