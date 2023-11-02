(ns exco.workspace-io.core
  (:refer-clojure :exclude [read read-string print ref])
  (:require [clojure.edn :as edn]
            [clojure.core :as clj]
            [clojure.pprint :as pp]
            [exco.fs.interface :as fs]
            [exco.workspace.interface :as ws]))

(declare read print opts)

(defn read-ref
  [file]
  (with-open [reader (fs/reader file)]
    (edn/read opts reader)))

(defn ref
  ([file]
   (ref file (read-ref file)))
  ([file val]
   (with-meta val
     {:type :io/ref
      :file file})))

(defn env
  [var]
  (with-meta (System/getenv var)
    {:type :io/env
     :var var}))

(def opts
  {:readers {'ref ref
             'env env}})

(defn read
  ([] (read *in*))
  ([stream] (ws/validate! (edn/read opts stream))))

(defn read-string
  [str]
  (with-in-str str (read opts)))

(defn read-file
  [file]
  (with-open [reader (fs/reader file)]
    (read reader)))

(defn print-file
  [value file]
  (with-open [writer (fs/writer file)]
    (binding [*out* writer]
      (print value))))

(defn print-ref
  [value file]
  (with-open [writer (fs/writer file)]
    (binding [*out* writer]
      (pp/pprint (vary-meta value (constantly nil))))))

(defmulti dispatch
  (comp :type meta))

(defmethod dispatch :default
  [val]
  (pp/simple-dispatch val))

(defmethod dispatch :io/env
  [val]
  (clj/print "#env")
  (pr (:var (meta val))))

(defmethod dispatch :io/ref
  [val]
  (let [file (-> val meta :file)]
    (print-ref val file)
    (clj/print "#ref ")
    (pr (:file (meta val)))))

(defn print
  ([val] (pp/with-pprint-dispatch dispatch (pp/pprint val)))
  ([val stream] (binding [*out* stream] (print val))))

(defn print-string
  [val]
  (with-out-str (print val)))

(defmacro with-base
  [dir & xs]
  `(fs/with-base ~dir ~@xs))
