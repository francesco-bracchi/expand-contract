(ns exco.patch.core
  (:require [clojure.spec.alpha :as s]
            [exco.patch.spec]
            [exco.hunk.interface :as hunk]))

;;todo: make patch like #:patch{hunks: []}?
(def zero
  [])

(defn inverse
  [patch]
  (->> patch
       (map hunk/inverse)
       reverse))

(defn hunk-push
  [ps q]
  (loop [ps ps q q rs []]
    (if (empty? ps) (concat [q] rs)
        (let [ps' (butlast ps) p (last ps) ]
          (if-let [x (hunk/coalesce p q)]
            (if (= x :hunk/nihil)
              (concat ps' rs)
              (recur ps' x rs))
            (if-let [[q' p'] (hunk/commute p q)]
              (recur ps' q' (vec (cons p' rs)))
              (concat ps [q] rs)))))))

(defn patch-merge
  [ps [q & qs]]
  (if q
    (recur (hunk-push ps q) qs)
    ps))

(defn compose
  [p & ps]
  (vec (reduce patch-merge p ps)))

(s/fdef compose
  :args (s/+ :patch/t)
  :ret :patch/t)

(s/fdef inverse
  :args (s/cat :p :patch/t)
  :ret :patch/t)
