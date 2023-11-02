(ns exco.workspace-io.interface
  (:refer-clojure :exclude [read read-string print ref])
  (:require [exco.workspace-io.core :as core]))

(defn ref
  ([file]
   (core/ref file))
  ([file val]
   (core/ref file val)))

(defn env
  [var]
  (core/env var))

(defn read
  ([stream] (core/read stream))
  ([] (core/read)))

(defn print
  ([data] (core/print data))
  ([data stream] (core/print data stream)))

(defn read-string
  [str]
  (core/read-string str))

(defn print-string
  [val]
  (core/print-string val))

(defn read-file
  [file]
  (core/read-file file))

(defn print-file
  [value file]
  (core/print-file value file))

(defmacro with-base
  [dir & xs]
  `(core/with-base ~dir ~@xs))
