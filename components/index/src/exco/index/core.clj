(ns exco.index.core
  (:require [clojure.spec.alpha :as s]
            [exco.index.spec]))

(defn valid?
  [descr]
  (s/valid? :index/t descr))

(defn validate!
  [descr]
  (when-not (valid? descr)
    (throw (ex-info "invalid index" {:reason (s/explain-data :index/t descr)}))))

(defn args
  [arg]
  (cond
    (s/valid? :index/args arg) arg
    (s/valid? :index/arg arg) [arg]
    (s/valid? :arg/expression arg) [#:arg{:expression arg}]
    :else (throw (ex-info "wrong argument" {:arg arg}))))

(defn make
  [table as]
  #:index{:table table :args (args as)})

(defn add-arguments
  [descr as]
  (update descr :index/args concat (args as)))
