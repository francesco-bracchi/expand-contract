(ns exco.migrator.interface.spec
  (:require [clojure.spec.alpha :as s]
            [exco.binding.interface.spec]
            [exco.migrator.interface :as i]))

(s/def :migrator/type
  #{:local})

;; todo: check jdbc syntax
(s/def :migrator/db-url
  string?)

(defmulti opts
  :migrator/type)

(s/def :migrator/opts
  (s/multi-spec opts :migrator/type))

(s/def :migrator/with-fn
  (s/fspec :args (s/cat :m :migrator/t :fun (s/fspec :args (s/cat)))))

(s/def :migrator/init-fn
  (s/fspec :args (s/cat)))

(s/def :migrator/bind-fn
  (s/fspec :args (s/cat :env :binding/env :prj :binding/project :con :binding/conn)))

(s/def :migrator/unbind-fn
  (s/fspec :args (s/cat :env :binding/env :prj :binding/project)))

(s/def :migrator/get-fn
  (s/fspec :args (s/cat :env :binding/env :prj :binding/project)
           :ret :binding/project))

(defmethod opts :local
  [_]
  (s/keys
   :req [:migrator/db-url]))

(s/def :migrator/t
  (s/keys :req [:migrator/with-fn
                :migrator/init-fn
                :migrator/bind-fn
                :migrator/unbind-fn
                :migrator/get-fn]))

(s/fdef i/with
  :args (s/cat :opts any?
               :body (s/* any?)))

(s/fdef i/bind
  :args (s/cat :prj :binding/project
               :env :binding/env
               :con :binding/conn))

(s/fdef i/get
  :args (s/cat :prj :binding/project
               :env :binding/env)
  :ret :binding/conn)
