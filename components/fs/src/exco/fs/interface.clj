(ns exco.fs.interface
  (:require [exco.fs.core :as core]))

(defn extension
  [file]
  (core/extension file))

(defn directory
  [file]
  (core/directory file))

(defn filename
  [file]
  (core/filename file))

(defn but-extension
  [file]
  (core/but-extension file))

(defn ^java.io.File file
  [f]
  (core/file f))

(defn dir
  [& xs]
  (apply core/dir xs))

(defn mkdir
  [dir]
  (core/mkdir dir))

(defn exists?
  [file]
  (core/exists? file))

(defn ^java.io.Reader reader
  [f]
  (core/reader f))

(defn ^java.io.Writer writer
  [f]
  (core/writer f))

(defmacro with-base
  [base & xs]
  `(core/with-base ~base ~@xs))
