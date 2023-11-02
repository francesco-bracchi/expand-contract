(ns exco.command.core
  (:require [clojure.spec.alpha :as s]
            [exco.command.spec]))

(defn valid?
  [cmd]
  (s/valid? :command/t cmd))

(defn validate!
  [cmd]
  (when-not (valid? cmd)
    (throw (ex-info "wrong command" {:reason (s/explain-data :command/t cmd)})))
  cmd)

(defn parse-pair
  [[k v]]
  (when-not (keyword? k) (throw (ex-info "even arguments must be keywords" {:keyword k})))
  [k v])

(defn parse-args
  [as]
  (when (odd? (count as)) (throw (ex-info "odd number-of arguments" {:arguments as})))
  (->> as
       (map read-string)
       (partition 2)
       (map parse-pair)
       (into {})))

(defn parse
  [[a & xs]]
  (let [a' (keyword a)
        xs' (parse-args xs)]
    (validate! (assoc xs' :action a'))))
