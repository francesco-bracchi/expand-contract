(ns exco.migration-log.interface.spec
  (:require [exco.migration-log.interface :as i]
            [exco.binding.interface.spec]
            [clojure.spec.alpha :as s]))

(s/def :migration-log/id
  nat-int?)

(s/def :migration-log/binding-id
  nat-int?)

(s/def :migration-log/progressive
  nat-int?)

;; it's an hexadecilam
(s/def :migration-log/hash
  (s/and string? #(= (count %) 10)))

(s/def :migration-log/created-at
  (partial instance? java.time.Instant))

(s/def :migration-log/uid
  string?)

(s/def :migration-log/state
  #{"inactive"
    "expanding"
    "expanded"
    "contracting"
    "contracted"})

(s/def :migration-log/t
  (s/keys :req [:migration-log/binding-id
                :migration-log/progressive
                :migration-log/hash
                :migration-log/uid
                :migration-log/state]
          :opt [:migration-log/id
                :migration-log/created-at]))

(def query map?)

(s/fdef i/bound-to
  :args (s/cat :query ::query
               :project :binding/project
               :env :binding/env)
  :ret query)

(s/fdef i/sorted
  :args (s/cat :query ::query)
  :ret query)

(s/fdef i/with-states
  :args (s/cat :query ::query :states (s/coll-of :migration-log/states))
  :ret query)

(s/fdef i/with-states
  :args (s/cat :query ::query :state :migration-log/states)
  :ret query)

(s/fdef i/insert
  :args (s/* :migration-log/t)
  :ret query)
