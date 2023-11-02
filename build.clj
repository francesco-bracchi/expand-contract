(ns build
  "The build script for the example of the poly documentation.

   Targets:
   * uberjar :project PROJECT
     - creates an uberjar for the given project

   For help, run:
     clojure -A:deps -T:build help/doc

   Create uberjar for command-line:
     clojure -T:build uberjar :project command-line

   Create a nativeimage for command-line:
     clojure -T:build native :project command-line"
  (:require [clojure.string :as s]
            [clojure.java.io :as io]
            [clojure.tools.build.api :as b]
            [clojure.tools.deps :as t]
            [clojure.tools.deps.util.dir :refer [with-dir]]))

(defn- get-project-aliases []
  (let [edn-fn (juxt :root-edn :project-edn)]
    (-> (t/find-edn-maps)
        (edn-fn)
        (t/merge-edns)
        :aliases)))

(defn- ensure-project-root
  "Given a task name and a project name, ensure the project
   exists and seems valid, and return the absolute path to it."
  [task project]
  (let [project-root (str (System/getProperty "user.dir") "/projects/" project)]
    (when-not (and project
                   (.exists (io/file project-root))
                   (.exists (io/file (str project-root "/deps.edn"))))
      (throw (ex-info (str task " task requires a valid :project option") {:project project})))
    project-root))

(defn uberjar
  "Builds an uberjar for the specified project.

   Options:
   * :project - required, the name of the project to build,
   * :uber-file - optional, the path of the JAR file to build,
     relative to the project folder; can also be specified in
     the :uberjar alias in the project's deps.edn file; will
     default to target/PROJECT.jar if not specified.

   Returns:
   * the input opts with :class-dir, :compile-opts, :main, and :uber-file
     computed.

   The project's deps.edn file must contain an :uberjar alias
   which must contain at least :main, specifying the main ns
   (to compile and to invoke)."
  [{:keys [project uber-file] :as opts}]
  (let [project-root (ensure-project-root "uberjar" project)
        aliases      (with-dir (io/file project-root) (get-project-aliases))
        main         (-> aliases :uberjar :main)]
    (when-not main
      (throw (ex-info (str "the " project " project's deps.edn file does not specify the :main namespace in its :main alias")
                      {:aliases aliases})))
    (b/with-project-root project-root
      (let [class-dir "target/classes"
            uber-file (or uber-file
                          (-> aliases :uberjar :uber-file)
                          (str "target/" project ".jar"))
            opts      (merge opts
                             {:basis        (b/create-basis)
                              :class-dir    class-dir
                              :compile-opts {:direct-linking true}
                              :main         main
                              :ns-compile   [main]
                              :uber-file    uber-file})]
        (b/delete {:path class-dir})
        ;; no src or resources to copy
        (println "Compiling" (str main "..."))
        (b/compile-clj opts)
        (println "Building uberjar" (str uber-file "..."))
        (b/uber opts)
        (b/delete {:path class-dir})
        (println "Uberjar is built.")
        opts))))

(defn- module-name
  [main]
  (s/replace main #"-" "_"))

(defn native
  "builds a native image for the :project"
  [{:keys [project native-file] :as opts}]
  (let [opts (uberjar opts)
        project-root (ensure-project-root "uberjar" project)
        ;; aliases      (with-dir (io/file project-root) (get-project-aliases))
        main         (:main opts)
        uber-file    (:uber-file opts)
        native-file  (or native-file (str "target/" project ".cmd"))
        opts' (assoc opts :native-file native-file)]
    (when-not main
      (throw (ex-info (str "the " project " main is missing") opts)))
    (when-not uber-file
      (throw (ex-info (str "the " project " project's uberfile is not found"))))
    (b/with-project-root project-root
      (println (str "Native compiling " native-file "..."))
      (println (str "native-image "
                    "--no-fallback "
                    "--diagnostics-mode "
                    "--report-unsupported-elements-at-runtime "
                    "--initialize-at-build-time "
                    "-H:+ReportExceptionStackTraces "
                    (str "-H:Name=./" native-file " ")
                    "-jar" uber-file " "
                    (module-name main)))
      (b/process {:command-args ["native-image"
                                 "--diagnostics-mode"
                                 "--report-unsupported-elements-at-runtime"
                                 "--initialize-at-build-time"
                                 "-H:+ReportExceptionStackTraces"
                                 "-jar" uber-file
                                 native-file]})
      (b/delete {:path "reports"})
      opts')))

(defn clean
  "clean a :project output file"
  [{:keys [project] :as opts}]
  (let [project-root (ensure-project-root "uberjar" project)]
    (b/with-project-root project-root
      (print  (str "cleaning ") (str project) "...")
      (b/delete {:path "target"})
      (print  " done\n"))
    opts))
