(ns exco.fs.core
  (:import [java.io File PushbackReader Reader Writer])
  (:require [clojure.java.io :as io]))

(def ^:dynamic *base*)

(defn dir
  [& xs]
  (apply str (interpose File/separator xs)))

(defn directory
  [^String str]
  (subs str 0 (.lastIndexOf str File/separator)))

(defn filename
  [^String str]
  (subs str (inc (.lastIndexOf str File/separator)) (count str)))

(defn extension
  [^String str]
  (subs str (inc (.lastIndexOf str ".")) (count str)))

(defn but-extension
  [^String str]
  (subs str 0 (.lastIndexOf str ".")))

(defn ^File file
  [f]
  (if (.isAbsolute (io/file f)) (io/file f)
      (io/file *base* f)))

(defn mkdir
  [f]
  (.mkdirs ^File (file f)))

(defn exists?
  [f]
  (.exists ^File (file f)))

(defn ^Reader reader
  [f]
  (-> f (file) (io/reader) (PushbackReader.)))

(defn ^Writer writer
  [f]
  (-> f (file) (io/writer)))

(defmacro with-base
  [base & xs]
  `(binding [*base* ~base] ~@xs))
