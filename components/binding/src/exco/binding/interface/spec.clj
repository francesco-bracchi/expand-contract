(ns exco.binding.interface.spec
  (:require [clojure.spec.alpha :as s]
            [exco.binding.interface :as i]))

(s/def ::query map?)

(s/def :binding/id
  nat-int?)

(s/def :binding/conn
  string?)

(s/def :binding/project
  string?)

(s/def :binding/env
  string?)

(s/def :binding/t
  (s/keys :req [:binding/project :binding/env :binding/conn]
          :opt [:binding/id]))

(s/fdef i/bind!
  :args (s/cat :project :binding/project
               :env :binding/env
               :conn :binding/conn))

(s/fdef i/with-project
  :args (s/cat :query ::query :project :binding/project)
  :ret ::query)

(s/fdef i/with-env
  :args (s/cat :query ::query :env :binding/env)
  :ret ::query)

(s/fdef i/get
  :args (s/cat :query ::query :project :binding/project :env :binding/env)
  :ret ::query)
