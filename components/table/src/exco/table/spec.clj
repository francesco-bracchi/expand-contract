(ns exco.table.spec
  (:require [clojure.spec.alpha :as s]
            [exco.column.interface]))

(s/def :table/columns
  (s/map-of keyword? :column/t))

(s/def ::primary-key
  (s/cat :pkey #{:primary-key}
         :cols (s/* keyword?)))

(s/def ::unique
  (s/cat :pkey #{:unique}
         :cols (s/* keyword?)))

(s/def ::check
  (s/cat :pkey #{:check}
         :exp any?))

(s/def ::foreign-key
  (s/cat :pkey #{:foreign-key}
         :cols (s/coll-of keyword?)
         :ref (s/cat :db keyword?
                     :cols (s/* keyword?))))

(s/def ::constraint
  (s/or :primary-key ::primary-key
        :unique ::unique
        :check ::check
        :foreign-key ::foreign-key))

(s/def :table/constraints
  (s/coll-of ::constraint))

(s/def :table/t
  (s/keys :req [:table/columns]
          :opt [:table/constraints]))


;; #:table{:columns {:id #:column{...},
;;                   :db #:column{...}}
;;         :constraints {:xxx [:primary-key :db :id]
;;                       :xxx [:check [:>= :x 10]]
;;                       :zzz [:unique :db :id ]
;;                       :yyy [:foreign-key [:id :db] [:databases :id_x :id_y]]}}

;; #:table{:columns {:id #:column{...},
;;                   :db #:column{...}}
;;         :constraints {:xxx #primary-key [:db :id]
;;                       :xxx #check [:>= :x 10]
;;                       :zzz #unique [:db :id]
;;                       :yyy #foreign-key [[:id :db] [:databases :id_x :id_y]]
