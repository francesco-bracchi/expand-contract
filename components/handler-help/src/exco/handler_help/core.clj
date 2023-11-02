(ns exco.handler-help.core
  (:require [clojure.spec.alpha :as s]
            [exco.workspace-io.interface :as io]
            [exco.defaults.interface :as defaults]
            [exco.fs.interface :as fs]
            [exco.command.interface :as command]))

(def default-args
  {:directory defaults/directory
   :workspace-file defaults/workspace-file})

(defn spec-of
  [action]
  (->> {:action action}
       (command/spec-of)
       (s/describe)
       rest
       (apply hash-map)))

(defn print-fields
  [fs]
  (doseq [f fs]
    (print "  ")
    (prn (keyword (name f)) (s/describe f))))

(defn print-required
  [fs]
  (if (seq fs)
    (do (println "required fields:") (print-fields fs))
    (println "no required fields.")))

(defn print-optional
  [fs]
  (if (seq fs)
    (do (println "optional fields:") (print-fields fs))
    (println "no optional fields.")))

(defn help-command
  [action]
  (let [spec (spec-of action)]
    (println "command:" (name action))
    (print-required (remove (partial = :action) (:req-un spec)))
    (print-optional (:opt-un spec))))

(defn help-commands
  []
  (doseq [action command/actions]
    (help-command action)))

(defn help-ws
  [{:workspace/keys [default-db databases revision]} {:keys [workspace-file]}]
  (println "workspace version:" revision)
  (println "workspace file:" (.getAbsolutePath (fs/file workspace-file)))
  (print "available databases: ") (->> databases keys (map name) (apply prn))
  (print "default database: ") (prn (name default-db)))

(defn help
  [ws cmd]
  (help-ws ws cmd)
  (println)
  (help-commands))

(defn handle*
  [{:keys [directory workspace-file] :as cmd}]
  (fs/with-base directory
    (help (io/read-file workspace-file) cmd)))

(defn handle
  [cmd]
  (handle* (merge default-args cmd)))
