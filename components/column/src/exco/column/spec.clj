(ns exco.column.spec
  (:require [clojure.spec.alpha :as s]))

(s/def :column/basic-type
  #{:type/int
    :type/boolean
    :type/float
    :type/text
    :type/binary
    :type/instant
    :type/uuid
    :type/json})

(s/def :column/array-type
  (s/cat :tag #{:type/array}
         :type :column/type
         :dimensions pos-int?))

(s/def :column/udt-type
  (s/cat :tag #{:type/user-defined}
         :v string?))

(s/def :column/type
  (s/or :basic :column/basic-type
        :udt :column/udt-type
        :comp :column/array-type))

(s/def :column/nullable?
  boolean?)

(s/def :column/default
  any?)

(s/def :column/check
  any?)

(s/def :column/checks
  (s/coll-of :column/check))

(s/def :column/unique?
  boolean?)

(s/def :column/primary-key?
  boolean?)

(s/def :reference/name
  (s/or :kw keyword?
        :sym symbol?
        :str (s/and string? seq)))

(s/def :reference/table
  :reference/name)

(s/def :reference/column
  :reference/name)

(s/def :column/reference
  (s/keys :req [:reference/table :reference/column]))

;; TODO: encode taht nullable? + unique? is mutex primaty-key?
(s/def :column/t
  (s/keys :req [:column/type]
          :opt [:column/nullable?
                :column/unique?
                :column/primary-key?
                :column/checks
                :column/default
                :column/reference]))
