(ns exco.patch-apply.hunk.spec
  (:require [clojure.spec.alpha :as s]
            [exco.schema.interface]
            [exco.patch-apply.hunk.spec]))

(s/def :patch-apply/schema
  :schema/t)

(s/def :patch-apply/errors
  (s/coll-of :check/t))

(s/def :patch-apply/right
  (s/keys :req-un [:patch-apply/schema]))

(s/def :patch-apply/left
  (s/keys :req-un [:patch-apply/errors]))

(s/def :patch-apply/either
  (s/or :left :patch-apply/left
        :right :patch-apply/right))
