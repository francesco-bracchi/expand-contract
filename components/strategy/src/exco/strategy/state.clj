(ns exco.strategy.state
  (:refer-clojure :exclude [swap!]))

(def ^:dynamic *main-ns*)
(def ^:dynamic *next-ns*)
(def ^:dynamic *bindings*)


(defmacro with
  [b & xs]
  `(let [{curr# :curr next# :next main# :main} ~b]
     (binding [*main-ns* main#
               *next-ns* next#
               *curr-ns* curr#
               *bindings* {}]
       ~@xs)))

(defn swap!
  [fun & args]
  (set! *bindings* (apply fun *bindings* args)))

(defn bind!
  [k v]
  (swap! assoc k v))
