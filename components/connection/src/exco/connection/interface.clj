(ns exco.connection.interface
  (:refer-clojure :exclude [seq])
  (:require [exco.connection.core :as core]))

(defmacro with
  [ds & xs]
  `(core/with ~ds ~@xs))

(defn one!
  ([c] (core/one! c))
  ([c as] (core/one! c as)))

(defn many!
  ([c] (core/many! c))
  ([c as] (core/many! c as)))

(defn seq
  ([c] (core/seq c))
  ([c as] (core/seq  c as)))
