(ns exco.migrator.core
  (:refer-clojure :exclude [get])
  (:require [exco.migrator-local.interface :as local]))

(def ^:dynamic *migrator*)

(defmulti make :migrator/type)

(defmethod make :local
  [opts]
  (local/make opts))

(defmethod make :default
  [opts]
  (throw (ex-info "unknown migrator type" {:opts opts})))

(defmacro with
  [opts & as]
  `(binding [*migrator* (make ~opts)]
     ((:migrator/with-fn *migrator*)
      (fn [] ~@as))))

(defn init
  ([] (init *migrator*))
  ([migrator] ((:migrator/init-fn migrator))))

(defn bind
  ([prj env conn] (bind *migrator* prj env conn))
  ([migrator prj env conn]
   ((:migrator/bind-fn migrator) prj env conn)))

(defn unbind
  ([prj env] (unbind *migrator* prj env))
  ([migrator prj env]
   ((:migrator/unbind-fn migrator) prj env)))

(defn get
  ([prj env] (get *migrator* prj env))
  ([migrator prj env]
   ((:migrator/get-fn migrator) prj env)))
