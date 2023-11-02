(ns exco.patch.spec
  (:require [clojure.spec.alpha :as s]
            [exco.hunk.interface]))

(s/def :patch/t
  (s/coll-of :hunk/t))
